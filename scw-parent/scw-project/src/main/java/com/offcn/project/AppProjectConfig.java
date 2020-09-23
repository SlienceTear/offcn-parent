package com.offcn.project;

import com.offcn.common.utils.OSSTemplate;
import org.junit.Before;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@MapperScan("com.offcn.project.mapper")
@EnableDiscoveryClient
@EnableSwagger2
public class AppProjectConfig  {


    public static void main(String[] args) {
        SpringApplication.run(AppProjectConfig.class);
    }

    @ConfigurationProperties(prefix = "oss") //加载属性文件为oss前缀的属性
    @Bean
    public OSSTemplate createOSSTemplate(){
        return new OSSTemplate();
    }
}