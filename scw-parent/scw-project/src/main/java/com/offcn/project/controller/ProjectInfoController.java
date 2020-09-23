package com.offcn.project.controller;

import com.offcn.common.response.AppResponse;
import com.offcn.common.utils.OSSTemplate;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectInfoService;
import com.offcn.project.vo.resp.ProjectDetailVo;
import com.offcn.project.vo.resp.ProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "项目基本功能模块（文件上传，项目信息获取等）")
@RequestMapping("/project")
@RestController
@Slf4j
public class ProjectInfoController {
    @Autowired
    private OSSTemplate ossTemplate;
    @Autowired
    private ProjectInfoService projectInfoService;

    @ApiOperation("文件上传功能")
    @PostMapping(value = "/upload")
    public AppResponse<Map<String,Object>> upload(@RequestParam("file")MultipartFile[] files){
        //创建返回集
        Map<String,Object> result=new HashMap<>();
        //创建集合储存所有的上传成功文件名
       List<String> list = new ArrayList<>();
       //判断上传的文件是否有数据
        if(files!=null&&files.length>0){
            for (MultipartFile file : files) {
                //判断文件数组的具体文件是否为空
                if(!file.isEmpty()){
                    try {
                        //调用ossclient执行上传到oss
                        String uploadFileName = ossTemplate.upload(file.getInputStream(), file.getOriginalFilename());
                        //文件名添加到集合
                        list.add(uploadFileName);

                    } catch (IOException e) {
                        e.printStackTrace();
                        //上传失败
                        return AppResponse.fail(result);
                    }
                }
            }
        }
        result.put("urls",list);
        log.debug("ossTemplate上传:{},文件上传路径为：{}",ossTemplate,list);
        //返回成功结果
        return AppResponse.ok(result);
    }
    @ApiOperation("获取项目回报列表")
    @GetMapping("/detail/returns/{projectId}")
    public AppResponse<List<TReturn>> detailReturn(@PathVariable("projectId")Integer projectId){
        List<TReturn> returns = projectInfoService.getProjectReturn(projectId);
        return AppResponse.ok(returns);
    }
    @ApiOperation("获取全部项目")
    @GetMapping("/all")
    public AppResponse<List<ProjectVo>> findAll(){
        //创建项目vo的集合
        List<ProjectVo> projectVoList=new ArrayList<>();
        //调用业务方法查询全部项目
        List<TProject> projectList = projectInfoService.getAllProjects();
        //遍历项目集合
        for (TProject project : projectList) {
            //创建项目Vo对象
            ProjectVo projectVo = new ProjectVo();
            //复制项目对象属性值到项目Vo对象
            BeanUtils.copyProperties(project,projectVo);
            //根据项目编号读取项目配图
            List<TProjectImages> projectImagesList = projectInfoService.getProjectImages(project.getId());

            //遍历配图集合，判断是否为头图，提取头图，设值给项目VO的配图
            for (TProjectImages projectImages : projectImagesList) {
                if(projectImages.getImgtype()== ProjectImageTypeEnume.HEADER.getCode()){
                    projectVo.setHeaderImage(projectImages.getImgurl());
                }
            }

            //把项目vo添加到vo集合
            projectVoList.add(projectVo);
        }

        return AppResponse.ok(projectVoList);
    }
    @ApiOperation("获取项目详情")
    @GetMapping("/detail/info/{projectId}")
    public AppResponse<ProjectDetailVo> detailInfo(Integer projectId){
        //根据项目编号获取项目基本信息
        TProject project = projectInfoService.getProjectInfo(projectId);
        //创建项目详情ID
        ProjectDetailVo projectDetailVo = new ProjectDetailVo();
        //复制整个项目的bean详情
        BeanUtils.copyProperties(project,projectDetailVo);
        //根据项目id获取配图
        List<TProjectImages> projectImagesList = projectInfoService.getProjectImages(projectId);
        //从项目详情Vo获取配图集合
        List<String> detailsImages = projectDetailVo.getDetailsImage();
        //判断配图集合是否为空，如果为空，初始化
        if (detailsImages==null){
            detailsImages=new ArrayList<>();
        }

        //遍历项目配图集合
        for (TProjectImages projectImages : projectImagesList) {
            //判断图片类型，等于头图，设置项目详情Vo头图属性值
            if(projectImages.getImgtype()==ProjectImageTypeEnume.HEADER.getCode()){
                projectDetailVo.setHeaderImage(projectImages.getImgurl());
            }else {
                //是详情图，加入到详情图集合
                detailsImages.add(projectImages.getImgurl());
            }
        }
        //设置详情图集合项目详情Vo
        projectDetailVo.setDetailsImage(detailsImages);
        //获取项目的回报
        List<TReturn> returnList = projectInfoService.getProjectReturn(projectId);
        //设置项目回报集合到详情Vo
        projectDetailVo.setProjectReturns(returnList);
        //返回相应对象封装
        return AppResponse.ok(projectDetailVo);
    }

    @ApiOperation("获取系统所有的项目标签")
    @GetMapping("/tags")
    public AppResponse<List<TTag>> tags() {
        List<TTag> tags = projectInfoService.getAllProjectTags();
        return AppResponse.ok(tags);
    }

    @ApiOperation("获取系统所有的项目分类")
    @GetMapping("/types")
    public AppResponse<List<TType>> types() {
        List<TType> types = projectInfoService.getProjectTypes();
        return AppResponse.ok(types);
    }

    @ApiOperation("获取回报信息")
    @GetMapping("/returns/info/{returnId}")
    public AppResponse<TReturn> getTReturn(@PathVariable("returnId") Integer returnId){
        TReturn tReturn = projectInfoService.getReturnInfo(returnId);
        return AppResponse.ok(tReturn);
    }
}
