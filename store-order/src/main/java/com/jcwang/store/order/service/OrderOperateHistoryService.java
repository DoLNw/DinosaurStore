package com.jcwang.store.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:08:30
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

