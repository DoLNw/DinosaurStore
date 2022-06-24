package com.jcwang.store.ware.dao;

import com.jcwang.store.ware.entity.WareOrderTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存工作单
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:12:26
 */
@Mapper
public interface WareOrderTaskDao extends BaseMapper<WareOrderTaskEntity> {
	
}
