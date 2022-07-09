package com.jcwang.store.ware.dao;

import com.jcwang.store.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:12:26
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    default void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, Integer skuNum) {
        
    }

    Long getSkuStock(@Param("skuId") Long skuId);

    List<Long> selectWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
