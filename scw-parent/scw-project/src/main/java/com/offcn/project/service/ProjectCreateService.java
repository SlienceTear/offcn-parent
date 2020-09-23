package com.offcn.project.service;

import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.vo.req.ProjectRedisStorageVo;

public interface ProjectCreateService {
    /**
     * 初始化项目
     * @param memberId
     * @return
     */
    public String initCreateProject(Integer memberId);

    //保存项目信息到数据库
    public void saveProjectInfoToDb(ProjectStatusEnume enume, ProjectRedisStorageVo projectRedisStorageVo);
}
