package com.jcwang.store.ware.service.impl;

import com.jcwang.store.constant.WareConstant;
import com.jcwang.store.ware.service.WareSkuService;
import com.jcwang.store.ware.vo.MergeVo;
import com.jcwang.store.ware.entity.PurchaseDetailEntity;
import com.jcwang.store.ware.service.PurchaseDetailService;
import com.jcwang.store.ware.vo.PurchaseDoneVo;
import com.jcwang.store.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.Query;

import com.jcwang.store.ware.dao.PurchaseDao;
import com.jcwang.store.ware.entity.PurchaseEntity;
import com.jcwang.store.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0)
                        .or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        // 采购单是新建过来的
        if (purchaseId == null) {
            // 新建一个采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // TODO 需要确认采购单的状态是0或者1才可以合并
        // 采购单是里面包含了多个需求的，可以使用stream的filter实现
        // 不过filter之后，如果collect里面没有元素，那其实不能保存的，需要注意

        List<Long> items = mergeVo.getItems();
        Long finalPurchasedId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchasedId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        // 更新采购单的时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     *
     * @param list 采购单id（里面会有多个采购项（采购需求））
     * 注意此处采购单和采购项的区别奥！
     */
    @Override
    public void received(List<Long> list) {
        // 1 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = list.stream().map(i -> {
            PurchaseEntity entity = this.getById(i);
            return entity;
        }).filter(item -> {
            return item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 2 改变采购单的状态
        // 上面filter之后，如果collect里面没有元素，那其实不能保存的，需要注意
        this.updateBatchById(collect);

        // 3 改变采购项的状态，注意需要根据采购单先拿到采购项目的id
        // 注意，买的话是根据采购单去买的，不过一个采购单会有多个采购项（采购需求，所以下面每次取出一个采购想）
        collect.forEach((item) -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchasedId(item.getId());

            List<PurchaseDetailEntity> collect1 = entities.stream().map(entity -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        // 1 改变采购单状态（若有一个采购项目没有完成，那么采购单不能标志完成）
        Long id = purchaseDoneVo.getId();

        List<PurchaseDetailEntity> updates = new ArrayList<>();

        // 2 改变采购单中每一个采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
            } else {
                // 3 将成功采购的采购单进行入库，库存数量增加
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }


            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setStatus(item.getStatus());
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

        // 改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() :
                WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}