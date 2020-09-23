package com.offcn.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {


    //配置文档基本信息
    @Bean
    public Docket getDocket(){
     return    new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.offcn.user.controller")).paths(PathSelectors.any()).build();
    }

    //创建配置属性对象：：文档标题、作者、联系
    private ApiInfo getApiInfo(){
      return   new ApiInfoBuilder().title("七易众筹-用户模块平台接口文档").description("为接口模块声明文档").contact("张三").version("1.0").termsOfServiceUrl("http://www.youjiuye.com").build();
    }
}
