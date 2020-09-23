package com.offcn.user.component;


import com.offcn.common.utils.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class SmsTemplate {

    //定义appCode
    @Value("${sms.appcode}")
    private String appcode;
    @Value("${sms.tpl_id}")
    private String tpl_id;

    //定义短信网关主机地址

    @Value("${sms.host}")
    private String host;
    @Value("${sms.path}")
    private String path;
    //设置请求方法默认值
    @Value("${sms.method:POST}")
    private String method;

    //发生短信程序
    //参数1：接收的手机号码  参数2：短信验证码
    public String smsSend(String mobile,String smscode){

        //创建一个请求头map集合
        Map<String,String> heads=new HashMap<String, String>();
        //把身份验证信息封装到请求头
        heads.put("Authorization","APPCODE "+appcode);

        //创建一个请求参数集合
        Map<String,String> querys=new HashMap<String, String>();
        //请求手机号码
        querys.put("mobile",mobile);
        //请求验证码的参数
        querys.put("param","code:"+smscode);
        //请求模板编号
        querys.put("tpl_id",tpl_id);

        //请求body的内容
        Map<String,String> bodys=new HashMap<String, String>();

        try {
            HttpResponse response=null;
            if(method.equalsIgnoreCase("POST")) {
                response = HttpUtils.doPost(host, path, method, heads, querys, bodys);

            }else {
                HttpUtils.doGet(host,path,method,heads,querys);
            }
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            System.out.println("短信发送返回结果:" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}