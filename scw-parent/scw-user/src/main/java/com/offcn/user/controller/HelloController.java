package com.offcn.user.controller;

import com.offcn.user.bean.User;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "第一个Swagger测试")
@RestController
public class HelloController {

    @ApiOperation("测试方法hello")
    @ApiImplicitParams(
            @ApiImplicitParam(name="name",value = "姓名",required = true)
    )
    @GetMapping("hello")
    public String hello(String name){
        return "hello:"+name;
    }

    @ApiOperation("查询指定用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", required = true),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true)
    }

    )
    @ApiResponse(code = 200,message = "查询用户成功")
    @GetMapping("findUser")
    public User findUser(String name,String email){
        User user = new User(1, name, email);
        return user;
    }
}
