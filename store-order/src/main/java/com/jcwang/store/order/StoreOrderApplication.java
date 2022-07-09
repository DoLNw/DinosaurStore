package com.jcwang.store.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 1 使用rabbitmq
 *   1 引入amqp依赖，rabbitautoconfigconfiguration就会自动生效
 *   2 给容器中配置了
 *       rabbittemplate、amqpAdmin、cachingconnectionfactory、rabbitmessageingtemplate
 *       所有的属性都是
 *       @ConfigurationProperties（prefix="spring。rabbitmq"）
 *   3 给配置文件中配置spring：rabbit信息
 *   4 @Enablerabbit，开启功能
 *   5 监听消息：使用@rabbirlistener(类或者方法上)，必须有@Enablerabbit
 *                 @rabbithandler 标在方法上，可以配合@rabbirlistener起到重载的作用。类上@rabbirlistener，方法@rabbithandler
 *
 *
 */

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(basePackages = "com.jcwang.store.order.feign")
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class StoreOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreOrderApplication.class, args);
	}

}
