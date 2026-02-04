package com.syj.eplus.module.pjms.api;

import com.syj.eplus.module.pjms.api.dto.ProjectDTO;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-31
 * @Description: 项目api
 */

public interface ProjectApi {

    /**
     * 获取项目DTO
     * @param id 项目id
     * @return 项目DTO
     */
    ProjectDTO getProjectDTOById(Long id);
}
