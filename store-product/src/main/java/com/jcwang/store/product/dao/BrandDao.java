package com.jcwang.store.product.dao;

import com.jcwang.store.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
