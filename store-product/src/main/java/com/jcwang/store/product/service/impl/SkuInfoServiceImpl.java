package com.jcwang.store.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.jcwang.store.product.entity.SkuImagesEntity;
import com.jcwang.store.product.entity.SpuInfoDescEntity;
import com.jcwang.store.product.feign.SeckillFeignService;
import com.jcwang.store.product.service.*;
import com.jcwang.store.product.vo.SeckillSkuVo;
import com.jcwang.store.product.vo.SkuItemSaleAttrVo;
import com.jcwang.store.product.vo.SkuItemVo;
import com.jcwang.store.product.vo.SpuItemAttrGroupVo;
import com.jcwang.store.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.Query;

import com.jcwang.store.product.dao.SkuInfoDao;
import com.jcwang.store.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private ThreadPoolExecutor executor;

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusByspuId(Long spuId) {

        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        // supply，结果别人可以用
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息的获取  pms_sku_info
            SkuInfoEntity info = this.getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);


        // 1 中弄完，3是需要1做完，所以用then
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);

        // 1 中弄完，4再做，其中4与3是并行的
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍    pms_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);

        // 1 中弄完，5再做，其中5与4与3是并行的
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5、获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);

        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> imagesEntities = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(imagesEntities);
        }, executor);

        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //3、远程调用查询当前sku是否参与秒杀优惠活动
            R skuSeckilInfo = seckillFeignService.getSkuSeckilInfo(skuId);
            if (skuSeckilInfo.getCode() == 0) {
                //查询成功
                SeckillSkuVo seckilInfoData = skuSeckilInfo.getData("data", new TypeReference<SeckillSkuVo>() {
                });
                skuItemVo.setSeckillSkuVo(seckilInfoData);

                if (seckilInfoData != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > seckilInfoData.getEndTime()) {
                        skuItemVo.setSeckillSkuVo(null);
                    }
                }
            }
        }, executor);

        //等到所有任务都完成
        CompletableFuture.allOf(saleAttrFuture, descFuture, seckillFuture, baseAttrFuture, imageFuture).get();

        return skuItemVo;

//        // Long spuId = info.getSpuId();
//        // Long catalogId = info.getCatalogId();
//
//        //2、sku的图片信息    pms_sku_images
//        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
//            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
//            skuItemVo.setImages(imagesEntities);
//        }, executor);
//
//        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
//            //3、远程调用查询当前sku是否参与秒杀优惠活动
//            R skuSeckilInfo = seckillFeignService.getSkuSeckilInfo(skuId);
//            if (skuSeckilInfo.getCode() == 0) {
//                //查询成功
//                SeckillSkuVo seckilInfoData = skuSeckilInfo.getData("data", new TypeReference<SeckillSkuVo>() {
//                });
//                skuItemVo.setSeckillSkuVo(seckilInfoData);
//
//                if (seckilInfoData != null) {
//                    long currentTime = System.currentTimeMillis();
//                    if (currentTime > seckilInfoData.getEndTime()) {
//                        skuItemVo.setSeckillSkuVo(null);
//                    }
//                }
//            }
//        }, executor);
//
//
//        //等到所有任务都完成
//        CompletableFuture.allOf(saleAttrFuture,descFuture,baseAttrFuture,imageFuture,seckillFuture).get();
//
//
//        return skuItemVo;
    }

}