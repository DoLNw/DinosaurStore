package com.jcwang.store.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author chenshun
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:07
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

