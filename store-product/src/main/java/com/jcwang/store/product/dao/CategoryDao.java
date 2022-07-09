package com.jcwang.store.product.dao;

import com.jcwang.store.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
