package com.jcwang.store.ware.service.impl;

import com.jcwang.store.ware.entity.PurchaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.Query;

import com.jcwang.store.ware.dao.PurchaseDetailDao;
import com.jcwang.store.ware.entity.PurchaseDetailEntity;
import com.jcwang.store.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    /**
     *
     * @param params statue 状态， wareId 仓库Id
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            purchaseDetailEntityQueryWrapper.eq("purchase_id", key)
                    .or().eq("sku_id", key);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            purchaseDetailEntityQueryWrapper.eq("status", status);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            purchaseDetailEntityQueryWrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
               purchaseDetailEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchasedId(Long id) {
        List<PurchaseDetailEntity> purchasedId = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));

        return purchasedId;
    }

}