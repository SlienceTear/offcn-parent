package com.offcn.order.controller;

import com.offcn.common.response.AppResponse;
import com.offcn.order.po.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "订单处理接口")
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("保存订单")
    @PostMapping("createOrder")
    public AppResponse<TOrder> createOrder(@RequestBody OrderInfoSubmitVo vo){

        //校验accessToken
        String memeberId = stringRedisTemplate.opsForValue().get(vo.getAccessToken());
        if(StringUtils.isEmpty(memeberId)){
            AppResponse<TOrder> appResponse = AppResponse.fail(null);
            appResponse.setMsg("权限不足");
            return appResponse;
        }

        try {
            TOrder order = orderService.saveOrder(vo);
            return AppResponse.ok(order);
        } catch (Exception e) {
            e.printStackTrace();

            return AppResponse.fail(null);
        }


    }
}