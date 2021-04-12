package com.emoney.pointweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.emoney.pointweb.repository.dao.mapper"})
@SpringBootApplication(scanBasePackages = {"com.emoney.pointweb","com.emoeny.pointcommon"})
public class PointWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointWebApplication.class, args);
    }

}
