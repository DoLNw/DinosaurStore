package com.jcwang.store.product.dao;

import com.jcwang.store.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku信息
 * 
 * @author chenshun
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:07
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {
	
}
