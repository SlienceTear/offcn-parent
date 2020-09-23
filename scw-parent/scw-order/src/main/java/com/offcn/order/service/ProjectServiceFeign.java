package com.offcn.order.service;

import com.offcn.common.response.AppResponse;
import com.offcn.order.service.impl.ProjectServiceFeignException;
import com.offcn.order.vo.resp.TReturn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
@FeignClient(value = "SCW-PROJECT",fallback = ProjectServiceFeignException.class)
public interface ProjectServiceFeign {
    /**
     * 调用获取指定回报编号，回报信息
     * @param returnId
     * @return
     */
    @GetMapping("/project/returns/info/{returnId}")
    public AppResponse<TReturn> findReturnInfo(Integer returnId);
}
