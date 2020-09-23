package com.offcn.user.service;

import com.offcn.common.response.AppResponse;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.vo.resp.UserRespVo;

import java.util.List;

public interface UserService {

    public void RegisterUser(TMember member);

    //用户登录方法
    public TMember login(String username,String password);

    //根据id查询信息
    public TMember findOne(Integer id);
    /**
     * 获取用户收货地址
     * @param memberId
     * @return
     */
    List<TMemberAddress> addressList(Integer memberId);
}
