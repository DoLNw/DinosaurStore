package com.jcwang.store.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.product.entity.CategoryEntity;

import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

