package com.emoney.pointweb.service.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/10 10:27
 */
@Configuration
public class RedissonConfig {


    @Value("${spring.redis1.hostName}")
    private String redis1HostName;

    @Value("${spring.redis1.port}")
    private String redis1Port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redis1HostName + ":" + redis1Port).setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}

