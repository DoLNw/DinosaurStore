package com.jcwang.store.coupon.dao;

import com.jcwang.store.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 10:51:22
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}
