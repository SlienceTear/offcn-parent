package com.offcn.project.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.common.response.AppResponse;
import com.offcn.common.vo.BaseVo;
import com.offcn.project.constants.ProjectConstants;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.po.TReturn;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目的功能创建模块")
@Slf4j
@RequestMapping("/project")
@RestController
public class ProjectCreateController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectCreateService projectCreateService;
    @ApiOperation("项目发起第一步同意协议")
    @GetMapping("/init")
    public AppResponse<String> init(BaseVo vo) {
        //根据用户token，去redis读取身份信息
        String memberId = stringRedisTemplate.opsForValue().get(vo.getAccessToken());
        System.out.println(memberId);
        //判断令牌是否存在
        if (StringUtils.isEmpty(memberId)) {
            //令牌未读取到用户信息，操作不允许
            AppResponse<String> appResponse = AppResponse.fail(null);
            appResponse.setMsg("令牌未读取到用户信息，操作不允许");
            return appResponse;
        }

        //调用项目创建服务，执行项目初始化方法
        try {
            String projectToken = projectCreateService.initCreateProject(Integer.parseInt(memberId));
            return AppResponse.ok(projectToken);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return AppResponse.fail(e.getMessage());
        }
    }

    //项目创建第二步：收集用户表单填写项目基本信息，存储更新到redis

    @ApiOperation("项目创建第二步：收集用户表单填写项目基本信息")
    @PostMapping("savebaseInfo")
    public AppResponse<String> saveBaseInfo(ProjectBaseInfoVo vo){
        //1、从redis读取 项目信息
        String jsonStr=    stringRedisTemplate.opsForValue().get(ProjectConstants.TEMP_PROJECT_PREFIX+vo.getProjectToken());
        //把读取到项目信息json字符串转换为 项目临时对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(jsonStr, ProjectRedisStorageVo.class);
        //把表单收集vo属性值，设置到项目临时对象
        BeanUtils.copyProperties(vo,projectRedisStorageVo);

        //转换项目临时对象为json字符串
        String toJSONString = JSON.toJSONString(projectRedisStorageVo);
        //更新保存到redis
        stringRedisTemplate.opsForValue().set(ProjectConstants.TEMP_PROJECT_PREFIX+vo.getProjectToken(),toJSONString);

        return AppResponse.ok("OK");
    }

    @ApiOperation("项目发起第3步-项目保存项目回报信息")
    @PostMapping("savereturn")
    public AppResponse<Object> saveReturnInfo(@RequestBody List<ProjectReturnVo> pro){

        //1、从项目回报集合，提取项目临时token
        if(pro!=null&&pro.size()>0){
            //提取第一个项目回报vo信息
            ProjectReturnVo projectReturnVoTemp = pro.get(0);
            //提取项目临时token
            String projectToken = projectReturnVoTemp.getProjectToken();

            //根据项目临时token，去redis读取项目 临时对象
            String jsonStr=    stringRedisTemplate.opsForValue().get(ProjectConstants.TEMP_PROJECT_PREFIX+projectToken);

            //转换为项目临时对象
            ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(jsonStr, ProjectRedisStorageVo.class);

            //创建一个集合存储项目回报对象Treturn
            List<TReturn> returnList=new ArrayList<>();
            //遍历项目回报集合
            for (ProjectReturnVo projectReturnVo : pro) {
                TReturn tReturn = new TReturn();
                BeanUtils.copyProperties(projectReturnVo,tReturn);
                //把项目回报对象Treturn存储到集合
                returnList.add(tReturn);
            }
            //关联设置项目回报，到项目临时存储对象
            projectRedisStorageVo.setProjectReturns(returnList);

            //转换为json字符串
            String jsonStringNew = JSON.toJSONString(projectRedisStorageVo);
            //更新保存到redis
            stringRedisTemplate.opsForValue().set(ProjectConstants.TEMP_PROJECT_PREFIX+projectToken,jsonStringNew);

            return AppResponse.ok("OK");
        }else {
            return AppResponse.fail("Fail");
        }
    }




    /**
     *项目保存第四步：保存项目信息到数据库
     * @param accessToken  登录验证token
     * @param projectToken  项目临时token
     * @param ops   操作类型
     * @return
     */
    @ApiOperation("项目保存第四步：保存项目信息到数据库")
    @PostMapping("submit")
    public AppResponse<Object> submit(String accessToken,String projectToken,String ops){

        //1、根据accessToken去redis读取对应信息
        String memeberId=    stringRedisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isEmpty(memeberId)){
            return AppResponse.fail("无登录权限");
        }

        //2、根据项目token，去redis读取项目临时信息
        String jsonStr = stringRedisTemplate.opsForValue().get(ProjectConstants.TEMP_PROJECT_PREFIX + projectToken);

        //转换json字符串为项目临时信息对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(jsonStr, ProjectRedisStorageVo.class);

        //3、判断操作类型是否为空
        if(!StringUtils.isEmpty(ops)){
            //判断操作类型如果等于 0 临时保存草稿
            if(ops.equalsIgnoreCase("0")){
                ProjectStatusEnume statusEnume = ProjectStatusEnume.DRAFT;
                //调用接口，保存草稿
                projectCreateService.saveProjectInfoToDb(statusEnume,projectRedisStorageVo);
                return AppResponse.ok("OK");
            }else if(ops.equals("1")){
                ProjectStatusEnume statusEnume = ProjectStatusEnume.SUBMIT_AUTH;
                projectCreateService.saveProjectInfoToDb(statusEnume,projectRedisStorageVo);
                return AppResponse.ok("OK");
            }else {
                return AppResponse.fail("此操作类型不支持");
            }
        }else {
            return AppResponse.fail("操作类型不能存在");
        }


    }

}
