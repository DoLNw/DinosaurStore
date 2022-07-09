package com.jcwang.store.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.product.entity.BrandEntity;
import com.jcwang.store.product.entity.CategoryBrandRelationEntity;
import com.jcwang.store.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author 夏沫止水
 * @email HeJieLin@dinosaurstore.com
 * @date 2020-05-22 19:00:18
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存详细信息
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

