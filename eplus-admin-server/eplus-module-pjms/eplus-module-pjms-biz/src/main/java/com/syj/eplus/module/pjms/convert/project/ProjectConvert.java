package com.syj.eplus.module.pjms.convert.project;

import com.syj.eplus.module.pjms.api.dto.ProjectDTO;
import com.syj.eplus.module.pjms.controller.admin.project.vo.ProjectRespVO;
import com.syj.eplus.module.pjms.controller.admin.project.vo.ProjectSaveReqVO;
import com.syj.eplus.module.pjms.dal.dataobject.project.ProjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ProjectConvert {

        ProjectConvert INSTANCE = Mappers.getMapper(ProjectConvert.class);

        ProjectRespVO convert(ProjectDO projectDO);

        default ProjectRespVO convertProjectRespVO(ProjectDO projectDO){
                ProjectRespVO projectRespVO = convert(projectDO);
                return projectRespVO;
        }

    ProjectDO convertProjectDO(ProjectSaveReqVO saveReqVO);

        ProjectDTO convertProjectDTO(ProjectDO projectDO);
}