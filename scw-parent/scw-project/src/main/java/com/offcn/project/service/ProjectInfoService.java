package com.offcn.project.service;

import com.offcn.project.po.*;

import java.util.List;

public interface ProjectInfoService {
    /**
     * 获取项目回报列表
     * @param projectId
     * @return
     */
    List<TReturn> getProjectReturn(Integer projectId);

    /**
     * 获取项目图片
     * @param id
     * @return
     */
    List<TProjectImages> getProjectImages(Integer id);

    /**
     * 获取系统中所有的项目
     * @return
     */
    List<TProject> getAllProjects();

    /**
     * 获取项目信息
     * @param projectId
     * @return
     */
    TProject getProjectInfo(Integer projectId);

    /**
     * 获得项目标签
     * @return
     */
    List<TTag> getAllProjectTags();

    /**
     * 获取项目分类
     * @return
     */
    List<TType> getProjectTypes();

    /**
     * 获取回报信息
     * @param returnId
     * @return
     */
    TReturn getReturnInfo(Integer returnId);
}