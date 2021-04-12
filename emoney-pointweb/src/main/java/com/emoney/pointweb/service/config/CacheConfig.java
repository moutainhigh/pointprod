package com.emoney.pointweb.service.config;

import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class CacheConfig {

    @Autowired
    @Qualifier("redisTemplate1")
    private RedisTemplate redis1Template1;

    @Bean
    public RedisService redisCache1() {
        return new RedisService(redis1Template1);
    }

}
