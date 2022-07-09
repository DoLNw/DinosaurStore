package com.jcwang.store.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.product.vo.Catelog2Vo;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
public interface CategoryService extends IService<CategoryEntity> {

    public Long[] findCatelogPath(Long catelogId);

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void remoMenuByIds(List<Long> asList);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2Vo>> getCatalogJson();

    void updateCascade(CategoryEntity category);
}

