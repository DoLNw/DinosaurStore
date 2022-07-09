package com.jcwang.store.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
///        config.useClusterServers().addNodeAddress("");
        config.useSingleServer().setAddress("redis://124.222.48.192:6379").setPassword("123456");

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
