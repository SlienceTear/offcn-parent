package com.offcn.user.service.impl;

import com.offcn.common.response.AppResponse;
import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.po.TMemberAddressExample;
import com.offcn.user.po.TMemberExample;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.resp.UserRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     *
     */
    @Autowired
    private TMemberMapper memberMapper;
    @Autowired
    private TMemberAddressMapper memberAddressMapper;
    @Override
    public void RegisterUser(TMember member) {
        //1、检查要注册手机号码是否存在
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(member.getLoginacct());
        long count = memberMapper.countByExample(example);
        //如果count大于0，表示手机号码已经存在
        if(count>0){
            throw new UserException(UserExceptionEnum.LOGINACCT_EXIST);
        }

        //2、可以继续判断邮箱是否被注册，自己完成

        //3、密码加密 md5\Bcrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodepassword = passwordEncoder.encode(member.getUserpswd());
        member.setUserpswd(encodepassword);

        member.setUsername(member.getLoginacct());

        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");

        log.debug("插入用户数据:{}",member);
        //保存到数据库
        memberMapper.insertSelective(member);




    }

    @Override
    public TMember login(String username, String password) {

        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(username);

        List<TMember> memberList = memberMapper.selectByExample(example);

        if(memberList!=null&&memberList.size()>0){
            //提取第一个账号数据
            TMember member = memberList.get(0);

            //比对密码
            String userpswd = member.getUserpswd();
            //声明密码加密器
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            //比对密码是否一致
            //前面参数，明文密码，后面参数，加密密码
            boolean matches = passwordEncoder.matches(password, userpswd);

            return matches?member:null;
        }


        return null;
    }

    @Override
    public TMember findOne(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TMemberAddress> addressList(Integer memberId) {
        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(memberId);
        return memberAddressMapper.selectByExample(example);
    }


}
