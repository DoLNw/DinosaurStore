package com.jcwang.store.order;

import com.jcwang.store.order.entity.OrderEntity;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StoreOrderApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testRabbit() {
		rabbitTemplate.convertAndSend("order.release.order", new OrderEntity());
	}

}
