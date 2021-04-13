package com.emoney.pointweb.service.config;

import com.emoney.pointweb.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
    @Bean
    public LoginInterceptor loginInterceptor(){return new LoginInterceptor();};

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/")
                .addPathPatterns("/index")
                .addPathPatterns("/pointproduct")
                .addPathPatterns("/pointlimit")
                .addPathPatterns("/pointquotation")
                .addPathPatterns("/pointorder")
                .addPathPatterns("/pointtaskconfiginfo")
                .addPathPatterns("/pointannounce")
                .addPathPatterns("/pointsendrecord")
                .addPathPatterns("/pointsendrecord/pointSendRecordDetail");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
