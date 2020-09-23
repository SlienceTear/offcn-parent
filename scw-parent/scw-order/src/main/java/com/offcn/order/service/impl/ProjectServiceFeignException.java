package com.offcn.order.service.impl;

import com.offcn.common.response.AppResponse;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.resp.TReturn;
import org.springframework.stereotype.Component;

@Component
public class ProjectServiceFeignException implements ProjectServiceFeign {
    @Override
    public AppResponse<TReturn> findReturnInfo(Integer returnId) {
        AppResponse<Object> appResponse = AppResponse.fail(null);
        appResponse.setMsg("远程项目接口调用[获取回报详情]失败");
        return null;
    }
}
