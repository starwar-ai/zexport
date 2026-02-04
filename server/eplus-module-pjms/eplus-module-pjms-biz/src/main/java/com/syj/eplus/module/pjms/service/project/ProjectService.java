package com.syj.eplus.module.pjms.service.project;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.pjms.api.dto.ProjectDTO;
import com.syj.eplus.module.pjms.controller.admin.project.vo.*;

import javax.validation.Valid;

/**
 * 项目 Service 接口
 *
 * @author zhangcm
 */
public interface ProjectService {

    /**
     * 创建项目
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProject(@Valid ProjectSaveReqVO createReqVO);

    /**
     * 更新项目
     *
     * @param updateReqVO 更新信息
     */
    void updateProject(@Valid ProjectSaveReqVO updateReqVO);

    /**
     * 删除项目
     *
     * @param id 编号
     */
    void deleteProject(Long id);

    /**
     * 获得项目
     *
* @param  projectDetailReq 明细请求实体
     * @return 项目
     */
ProjectRespVO getProject( ProjectDetailReq projectDetailReq);

    /**
     * 获得项目分页
     *
     * @param pageReqVO 分页查询
     * @return 项目分页
     */
    PageResult<ProjectRespVO> getProjectPage(ProjectPageReqVO pageReqVO);
    /**
    * 获取流程标识
    *
    * @return 流程标识
    */
    String getProcessDefinitionKey();
    /**
    * 通过任务
    *
    * @param userId 用户编号
    * @param reqDTO  通过请求
    */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
    * 不通过任务
    *
    * @param userId 用户编号
    * @param reqDTO 不通过请求
    */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
    * 提交任务
    *
    * @param id 业务id
    * @param userId    用户id
    */
    void submitTask(Long id, Long userId);

    /**
    * 更新审核状态状态
    *
    * @param auditableId 审核业务id
    * @param auditStatus 审核状态
    */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    void finishManufacture(Long id);

    void rollbackFinishManufacture(Long id);

    void doneManufacture(ProjectDoneReqVO reqVo);

    PageResult<ProjectSimpleRespVO> getSimpleProjectPage(ProjectPageReqVO pageReqVO);

    /**
     * 获取项目DTO
     * @param id 项目id
     * @return 项目DTO
     */
    ProjectDTO getProjectDTOById(Long id);
}