package com.emoney.pointweb.service.config;

import com.emoney.pointweb.interceptor.NetworkIntercepter;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new NetworkIntercepter()) //网络拦截器，统一打印日志
                .connectionPool(new ConnectionPool(200, 100, TimeUnit.MINUTES))
                .build();
    }
}