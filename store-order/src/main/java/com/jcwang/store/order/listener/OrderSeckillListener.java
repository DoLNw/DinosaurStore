package com.jcwang.store.order.listener;

import com.jcwang.store.order.service.OrderService;
import com.jcwang.store.to.mq.SeckillOrderTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/

@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo orderTo, Channel channel, Message message) throws IOException {

        log.info("准备创建秒杀单的详细信息...");

        try {
            orderService.createSeckillOrder(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("该秒杀订单信息已经处理好了，可以点击支付来确认");
        } catch (Exception e) {
            log.error("秒杀订单有问题，以下是问题原因，可以手动拒绝消息确认，然后重新放回队列");
            log.error(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

//    @RabbitHandler
//    public void listener2(Object object, Channel channel, Message message) throws IOException {
//        log.info("未匹配到的方法");
//        log.info(object.toString());
//
//    }
}
