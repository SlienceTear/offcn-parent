package com.offcn.user.controller;

import com.alibaba.druid.util.StringUtils;
import com.offcn.common.response.AppResponse;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.resp.UserAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "获取用户信息接口")
@RestController
@RequestMapping("user")
public class UserInfoController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @ApiOperation("获取用户收货地址")
    @ApiImplicitParam(name = "accessToken",value = "token",required = true)
    @GetMapping("/info/address")
    public AppResponse<List<UserAddressVo>> address(String accessToken){

        //1、根据accesstoken从redis读取会员编号
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isEmpty(memberId)){
            return AppResponse.fail(null);
        }
        //2、根据用户id读取对应的地址
        List<TMemberAddress> memberAddressList = userService.addressList(Integer.parseInt(memberId));

        //3、遍历地址集合，重新封装为地址Vo集合
        List<UserAddressVo> voList=new ArrayList<>();
        for (TMemberAddress memberAddress : memberAddressList) {
            UserAddressVo addressVo = new UserAddressVo();
            //复制用户地址对象属性值到用户vo对象
            BeanUtils.copyProperties(memberAddress,addressVo);
            voList.add(addressVo);
        }

        return AppResponse.ok(voList);
    }
}
