package com.jcwang.store.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.jcwang.store.constant.ProductConstant;
import com.jcwang.store.product.entity.*;
import com.jcwang.store.product.feign.SearchFeignService;
import com.jcwang.store.product.feign.WareFeignService;
import com.jcwang.store.product.service.*;
import com.jcwang.store.to.SkuHasStockVo;
import com.jcwang.store.to.es.SkuEsModel;
import com.jcwang.store.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.Query;

import com.jcwang.store.product.dao.SpuInfoDao;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // SPU商品、SKU商品规格（比如不同颜色，不同之间内存的组合），
        // 一个spuid下可能有多个sku，然后根据spuid的到的attrlist是一个list，
        // 每一个sku对应一个list，每一个list里面有该sku的attr

        // 组装需要的数据
        // 1 查处当前spuId对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusByspuId(spuId);
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODO 4 查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        // 上面一个是吧属性id集合起来，下面是找到所有的es中的检索属性
        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);

        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrs = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        // TODO 1 发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            // 注意，这里面返回的是，该spuid商品的每一个规格是否有库存的列表
            R r = wareFeignService.getSkuHasStock(skuIds);
            TypeReference<List<SkuHasStockVo>> listTypeReference = new TypeReference<List<SkuHasStockVo>>() {};
            stockMap = r.getData(listTypeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存服务查询异常，原因：{}", e);
        }



        // 封装每一个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skus.stream().map((sku) -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);

            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            // TODO 2 热度评分。刚上架的商品，默认0
            esModel.setHotScore(0L);
            // TODO 3 查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            // 设置检索属性
            esModel.setAttrs(attrs);

            return esModel;
        }).collect(Collectors.toList());

        // TODO 5 将数据发给ES进行保存，用store-search来保存
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0) {
            // 成功
            // TODO 6 修改当前spu的状态
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败
            // TODO 7 重复调用？接口幂等性：重试机制？
            // feigh调用流程
            /**
             * 1 构造请求数据，将对象转换为json
             *      template = buildTemplateFromArgs.create（argv）
             * 2 发送请求进行执行（执行成功会解码相应数据）
             *      executeAndDecode（template）
             * 3 执行请求会有重试机制
             *      在外围，有一个while（true）重试器
             *      while (true) {
             *          try {
             *              executeAndDecode(template);
             *          } catch() {
             *              try (retryer.continueOrPropagate(e);) catch(ex){ throw ex;}
             *              continue;
             *          }
             *      }
             */
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        SkuInfoEntity byId = skuInfoService.getById(skuId);
        Long spuId = byId.getSpuId();

        SpuInfoEntity entity = getById(spuId);

        return entity;
    }

}