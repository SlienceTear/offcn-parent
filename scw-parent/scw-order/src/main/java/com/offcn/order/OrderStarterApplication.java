package com.offcn.order;

import org.junit.jupiter.api.Order;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.offcn.order.mapper")
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker //开启熔断
public class OrderStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderStarterApplication.class,args);
    }
}
