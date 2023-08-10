package com.yupao.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private String port;
    private String host;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(3).setPassword("zhuzm0212.");
        // 2. Create Redisson instance
        // Sync and Async API
        return Redisson.create(config);
    }
}
