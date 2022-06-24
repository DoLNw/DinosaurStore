package com.jcwang.store.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:12:26
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

