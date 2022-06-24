package com.jcwang.store.ware.dao;

import com.jcwang.store.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:12:26
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
