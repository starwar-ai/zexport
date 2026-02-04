package com.syj.eplus.module.pjms.api.project;

import com.syj.eplus.module.pjms.api.ProjectApi;
import com.syj.eplus.module.pjms.api.dto.ProjectDTO;
import com.syj.eplus.module.pjms.service.project.ProjectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-31
 * @Description: 项目api实现类
 */
@Service
public class ProjectApiImpl implements ProjectApi {

    @Resource
    private ProjectService projectService;


    @Override
    public ProjectDTO getProjectDTOById(Long id) {
        if (Objects.isNull(id)){
            return null;
        }
       return projectService.getProjectDTOById(id);
    }
}
