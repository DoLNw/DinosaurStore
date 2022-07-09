package com.jcwang.store.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.order.vo.OrderConfirmVo;
import com.jcwang.store.order.vo.OrderSubmitVo;
import com.jcwang.store.order.vo.PayVo;
import com.jcwang.store.order.vo.SubmitOrderResponseVo;
import com.jcwang.store.to.mq.SeckillOrderTo;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:08:30
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    // 给订单确认页面返回需要用的数据
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayVo payVo);

    public void createSeckillOrder(SeckillOrderTo orderTo);
}

