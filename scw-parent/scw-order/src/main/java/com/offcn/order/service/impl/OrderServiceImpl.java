package com.offcn.order.service.impl;

import com.offcn.common.response.AppResponse;
import com.offcn.common.utils.AppDateUtils;
import com.offcn.order.enums.OrderStatusEnumes;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.po.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import com.offcn.order.vo.resp.TReturn;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProjectServiceFeign projectServiceFeign;

    @Override
    public TOrder saveOrder(OrderInfoSubmitVo vo) {
        //创建订单对象
        TOrder order = new TOrder();
        //1、提取accesstoken，从redis读取对应用户编号
        String memeberId = redisTemplate.opsForValue().get(vo.getAccessToken());

        if(!StringUtils.isEmpty(memeberId)){

            //2、设置订单所属用户编号
            order.setMemberid(Integer.parseInt(memeberId));
            //3、从订单vo复制相关属性值到订单对象
            BeanUtils.copyProperties(vo,order);

            //4、创建一个唯一值
            String orderNum = UUID.randomUUID().toString().replace("-", "");
            order.setOrdernum(orderNum);
            //5、设置订单创建时间
            order.setCreatedate(AppDateUtils.getFormatTime());

            //6、根据回报编号，获取回报详情
            AppResponse<TReturn> appResponse = projectServiceFeign.findReturnInfo(vo.getReturnid());

            //获取响应数据
            TReturn tReturn = appResponse.getData();
            //判断回报对象不为空，就设置订单金额
            if(tReturn!=null){
                //计算订单金额
                int orderMoney = tReturn.getSupportmoney() * vo.getRtncount() + tReturn.getFreight();
                //设置订单金额到订单对象
                order.setMoney(orderMoney);
            }

            //设置订单支付状态
            order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");


            //保存订单到数据库
            orderMapper.insert(order);


        }
        return null;
    }

}
