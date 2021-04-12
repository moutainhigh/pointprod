package com.emoney.pointweb.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class SpringSessionConfig {
    /**
     * SpringSession 中默认的序列化器为 jdk 序列化器，该序列化器效率低下，内存占用大。
     * 配置SpringSession序列化器
     * @return
     */
    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer setSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
