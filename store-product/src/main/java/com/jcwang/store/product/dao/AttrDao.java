package com.jcwang.store.product.dao;

import com.jcwang.store.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author chenshun
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:07
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
