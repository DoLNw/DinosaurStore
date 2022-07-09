package com.jcwang.store.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.jcwang.store.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class StoreAutoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreAutoServerApplication.class, args);
    }

}
