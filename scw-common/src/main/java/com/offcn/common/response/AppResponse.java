package com.offcn.common.response;

import com.offcn.common.enums.ResponseCodeEnume;

public class AppResponse<T> {

private Integer code;//定义响应状态码
private String msg;//响应消息
private T data;//泛型的响应数据

    //创建成功快速响应
    public static<T>  AppResponse<T> ok(T data){
        AppResponse<T> appResponse = new AppResponse<T>(ResponseCodeEnume.SUCCESS.getCode(), ResponseCodeEnume.SUCCESS.getMsg(), data);

        return appResponse;
    }

    //创建快速响应失败
    public static<T> AppResponse<T> fail(T data){
        AppResponse<T> response = new AppResponse<T>(ResponseCodeEnume.FAIL.getCode(), ResponseCodeEnume.FAIL.getMsg(), data);
        return response;
    }

    public AppResponse() {
    }

    public AppResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
