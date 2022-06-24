package com.jcwang.store.order.dao;

import com.jcwang.store.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:08:30
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
