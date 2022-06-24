package com.jcwang.store.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * openfeign
 * 远程调用别的服务
 *
 * 1.引入openfeign
 * 2.编写一个接口，告诉SpringCloud这个接口需要调用远程服务(见feign包下的service代码，首先@FeignClient中的回去注册中心找，然后找里面的方法)
 *   声明借口的每一个方法都是调用哪个远程服务的哪个请求
 * 3. 开启远程调用功能@EnableFeignClients，然后指定远程调用服务的接口在哪个包里面（此处在feign下）
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jcwang.store.member.feign")
public class DinosaurStoreMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinosaurStoreMemberApplication.class);
    }
}
