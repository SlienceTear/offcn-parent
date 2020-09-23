package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.project.constants.ProjectConstants;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
@Slf4j
public class ProjectCreateServiceImpl implements ProjectCreateService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;

    @Autowired
    private TProjectTagMapper projectTagMapper;

    @Autowired
    private TProjectTypeMapper projectTypeMapper;

    @Autowired
    private TReturnMapper returnMapper;
    @Override
    public String initCreateProject(Integer memberId) {

        //1、创建项目临时token
        String token = UUID.randomUUID().toString().replace("-", "");
        //2、创建项目存储到redis的对象
        ProjectRedisStorageVo vo = new ProjectRedisStorageVo();
        //设置项目属性，设置项目所属的用户编号
        vo.setMemberid(memberId);
        //设置项目临时token
        vo.setProjectToken(token);
        //把对象转换为json存储到redis
        String jsonString = JSON.toJSONString(vo);
        ////把项目JSON字符串存储到redis
        stringRedisTemplate.opsForValue().set(ProjectConstants.TEMP_PROJECT_PREFIX+token,jsonString);
        return token;
    }

    @Override
    public void saveProjectInfoToDb(ProjectStatusEnume enume, ProjectRedisStorageVo projectRedisStorageVo) {
        //1、创建项目基本对象
        TProject projectBase = new TProject();
        //复制项目临时对象的值到项目主对象
        BeanUtils.copyProperties(projectRedisStorageVo,projectBase);
        //从项目状态枚举对象提取项目状态，设置到项目基本对象
        projectBase.setStatus(enume.getCode()+"");

        //项目创建时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        projectBase.setCreatedate(dateFormat.format(new Date()));

        //设置相关信息初始化值 0
        projectBase.setFollower(0);
        projectBase.setCompletion(0);
        projectBase.setSupporter(0);
        projectBase.setSupportmoney(0L);

        //存储项目基本信息对象到数据库
        projectMapper.insertSelective(projectBase);

        //2、保存项目基本信息成功，获取到项目id
        Integer projectId = projectBase.getId();


        //3、获取项目头图
        String headerImageUrl = projectRedisStorageVo.getHeaderImage();
        //创建项目配图对象
        TProjectImages projectImagesHead = new TProjectImages(null, projectId, headerImageUrl, ProjectImageTypeEnume.HEADER.getCode());
        //保存项目头图到数据库
        projectImagesMapper.insert(projectImagesHead);

        //4、获取项目详情图
        List<String> detailsImageList = projectRedisStorageVo.getDetailsImage();
        if(detailsImageList!=null&&detailsImageList.size()>0){
            //遍历详情图集合
            for (String detailsImagesUrl : detailsImageList) {
                //创建项目配图对象（详情图）
                TProjectImages projectImagesDetails = new TProjectImages(null, projectId, detailsImagesUrl, ProjectImageTypeEnume.DETAILS.getCode());
                //保存详情图到数据库
                projectImagesMapper.insert(projectImagesDetails);
            }
        }

        //5、保存项目标签
        List<Integer> tagidsList = projectRedisStorageVo.getTagids();
        if(tagidsList!=null&&tagidsList.size()>0){
            for (Integer tagid : tagidsList) {
                //创建标签对象
                TProjectTag projectTag = new TProjectTag(null, projectId, tagid);
                //保存标签到数据库
                projectTagMapper.insert(projectTag);
            }
        }

        //6、保存分类
        List<Integer> typeidsList = projectRedisStorageVo.getTypeids();
        if(typeidsList!=null&&typeidsList.size()>0){
            for (Integer typeId : typeidsList) {
                //创建项目分类对象
                TProjectType projectType = new TProjectType(null, projectId, typeId);
                //保存项目分类到数据库
                projectTypeMapper.insert(projectType);
            }
        }
        //7、读取项目回报
        List<TReturn> projectReturnsList = projectRedisStorageVo.getProjectReturns();

        if(projectReturnsList!=null&&projectReturnsList.size()>0){
            for (TReturn tReturn : projectReturnsList) {
                //设置关联项目id
                tReturn.setProjectid(projectId);
                //保存项目回报到数据库
                returnMapper.insert(tReturn);
            }
        }

        //8、清除redis存储项目临时数据
        stringRedisTemplate.delete(ProjectConstants.TEMP_PROJECT_PREFIX+projectRedisStorageVo.getProjectToken());




    }

}
