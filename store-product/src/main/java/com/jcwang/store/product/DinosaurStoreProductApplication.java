package com.jcwang.store.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 整合mybatisplus
 * 导入依赖
 * 配置mysql数据源
 * 配置mubatisplus，使用mapperscan扫描mapper包，告诉mybatisplus，sql语句在哪里（yml中）
 */
@MapperScan("com.jcwang.store.product.dao")
@SpringBootApplication
public class DinosaurStoreProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinosaurStoreProductApplication.class, args);
    }
}
