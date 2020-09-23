package com.offcn.user.controller;

import com.offcn.common.enums.ResponseCodeEnume;
import com.offcn.common.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.po.TMember;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户注册登录处理类")
@RestController
@RequestMapping("user")
public class UserLoginController {


    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @ApiOperation("短信验证码发送")
    @ApiImplicitParam(name="phoneNo",value = "手机号",required = true)
    @PostMapping("sendCode")
    public AppResponse<Object> sendCode(String phoneNo){
        //1、创建短信验证码  333ss-ssss-s222-ss22-dd222
        String smscode = UUID.randomUUID().toString().substring(0, 4);
        //2、存储手机号和验证码到redis
        stringRedisTemplate.opsForValue().set(phoneNo,smscode,5, TimeUnit.MINUTES);
        //3、调用短信工具类，发送短信
        String smsSendResponse = smsTemplate.smsSend(phoneNo, smscode);
        if(smsSendResponse!=null&&smsSendResponse.indexOf("00000")>=0){
            return AppResponse.ok(smscode);
        }else {
            return AppResponse.fail(smsSendResponse);
        }
    }

    @ApiOperation("用户注册接口")
    @PostMapping("register")
    public AppResponse<Object> registerUser(UserRegistVo vo){

        //1、根据注册手机号，去redis提取对应验证码
        String smscodeRedis = stringRedisTemplate.opsForValue().get(vo.getLoginacct());

        //判断redis中验证码是否存在
        if(!StringUtils.isEmpty(smscodeRedis)){
            //比对redis验证码和用户表单输入验证码
            if(smscodeRedis.equalsIgnoreCase(vo.getCode())){
                //验证码一致，继续注册
                //创建用户实体对象
                TMember member = new TMember();
              //使用工具类复制vo对象属性值到实体对象
                BeanUtils.copyProperties(vo,member);
                //调用注册服务，执行注册
                try {
                    userService.RegisterUser(member);
                    //注册成功，删除短信验证码
                    stringRedisTemplate.delete(member.getLoginacct());
                    return AppResponse.ok(ResponseCodeEnume.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    return AppResponse.fail("注册失败，产生错误:"+e.getMessage());
                }
            }else {
                return AppResponse.fail("验证码不正确");
            }
        }else {
            return AppResponse.fail("验证码不存在，重新注册");
        }


    }

    @ApiOperation("用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户账号",required = true),
            @ApiImplicitParam(name = "password",value = "密码",required = true)
    })
    @PostMapping("login")
    public AppResponse<UserRespVo> login(String username, String password){
        TMember member = userService.login(username, password);
        //判断登录返回用户数据是否为空
        if(member==null){
            AppResponse<UserRespVo> appResponse = AppResponse.fail(null);
            appResponse.setMsg("用户登录失败");
            return appResponse;
        }

        //用户存在，登录成功
        //创建令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //把令牌存储到redis  键是 token 值是 用户id
        stringRedisTemplate.opsForValue().set(token,member.getId()+"",20000,TimeUnit.HOURS);
        //创建vo对象
        UserRespVo vo = new UserRespVo();

        //复制用户对象值到vo
        BeanUtils.copyProperties(member,vo);

        //设置令牌到vo
        vo.setAccessToken(token);

        return AppResponse.ok(vo);

    }
    @ApiOperation("根据用户id查询用户信息")
    @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "path")
    @GetMapping("/findUser/{id}")
    public AppResponse<UserRespVo> findOne(@PathVariable("id") Integer id){
        TMember tMember = userService.findOne(id);
        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(tMember,userRespVo);
        return AppResponse.ok(userRespVo);
    }
}
