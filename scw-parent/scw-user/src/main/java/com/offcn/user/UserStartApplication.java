package com.offcn.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.offcn.user.mapper")
@EnableSwagger2
public class UserStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserStartApplication.class,args);
    }
}
