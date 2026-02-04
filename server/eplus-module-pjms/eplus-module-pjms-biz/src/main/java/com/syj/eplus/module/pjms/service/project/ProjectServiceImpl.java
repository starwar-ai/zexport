package com.syj.eplus.module.pjms.service.project;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.enums.ChangeRecordTypeEnum;
import com.syj.eplus.framework.common.enums.PjmsProjectStatusEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pjms.api.dto.ProjectDTO;
import com.syj.eplus.module.pjms.controller.admin.project.vo.*;
import com.syj.eplus.module.pjms.convert.project.ProjectConvert;
import com.syj.eplus.module.pjms.dal.dataobject.project.ProjectDO;
import com.syj.eplus.module.pjms.dal.mysql.project.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pjms.enums.ErrorCodeConstants.PROJECT_DATE_NOT_EXISTS;
import static com.syj.eplus.module.pjms.enums.ErrorCodeConstants.PROJECT_NOT_EXISTS;


/**
 * 项目 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ProjectServiceImpl implements ProjectService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    private static final String PROCESS_DEFINITION_KEY = "pjms_project";

    private static final String CODE_PREFIX = "PJP";
    public static final String SN_TYPE = "SN_PJMS_PROJECT";
    public static final String OPERATOR_EXTS_KEY = "PjmsProject";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(ProjectSaveReqVO createReqVO) {
        if(Objects.nonNull(createReqVO.getPlanDate()) && createReqVO.getPlanDate().length>=2){
            createReqVO.setPlanStartDate(createReqVO.getPlanDate()[0]);
            createReqVO.setPlanEndDate(createReqVO.getPlanDate()[1]);
        }
        ProjectDO project = ProjectConvert.INSTANCE.convertProjectDO(createReqVO);
        project.setId(null);
        String categoryCode = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        project.setCode(categoryCode);
        project.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        project.setProjectStatus(PjmsProjectStatusEnum.PENDING_SUBMIT.getCode());
        projectMapper.insert(project);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(project.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增项目】%s", categoryCode));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, categoryCode);

        // 返回
        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ProjectSaveReqVO updateReqVO) {
        if(Objects.nonNull(updateReqVO.getPlanDate()) && updateReqVO.getPlanDate().length>=2){
            updateReqVO.setPlanStartDate(updateReqVO.getPlanDate()[0]);
            updateReqVO.setPlanEndDate(updateReqVO.getPlanDate()[1]);
        }
        // 校验存在
        ProjectDO projectDO = validateProjectExists(updateReqVO.getId());
        // 更新
        ProjectDO updateObj = ProjectConvert.INSTANCE.convertProjectDO(updateReqVO);

        projectMapper.updateById(updateObj);

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
        }

        //操作日志
        {
            List<ChangeRecord> changeRecords = new OperateCompareUtil<ProjectDO>().compare(projectDO, updateObj);

            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        }
    }

    @Override
    public void deleteProject(Long id) {
        // 校验存在
        validateProjectExists(id);
        // 删除
        projectMapper.deleteById(id);
    }

    private ProjectDO validateProjectExists(Long id) {
        ProjectDO projectDO = projectMapper.selectById(id);
        if (projectDO== null) {
            throw exception(PROJECT_NOT_EXISTS);
        }
        return  projectDO;
    }
    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long projectId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, projectId);
        updateAuditStatus(projectId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        projectMapper.updateById(ProjectDO.builder().id(auditableId).auditStatus(auditStatus).build());
        if(auditStatus.equals(BpmProcessInstanceResultEnum.PROCESS.getResult())){
            projectMapper.updateById(
                    ProjectDO.builder().id(auditableId)
                            .projectStatus(PjmsProjectStatusEnum.PENDING_APPROVE.getCode())
                            .build());
        }
        if(auditStatus.equals(BpmProcessInstanceResultEnum.APPROVE.getResult())){
            projectMapper.updateById(
                    ProjectDO.builder().id(auditableId)
                            .projectStatus(PjmsProjectStatusEnum.PROJECT.getCode())
                            .build());
        }
        if(auditStatus.equals(BpmProcessInstanceResultEnum.REJECT.getResult())){
            projectMapper.updateById(
                    ProjectDO.builder().id(auditableId)
                            .projectStatus(PjmsProjectStatusEnum.REJECT.getCode())
                            .build());
        }
    }

    @Override
    public void finishManufacture(Long id) {
        ProjectDO projectDO = projectMapper.selectOne(ProjectDO::getId,id);
        if(projectDO == null){
            throw exception(PROJECT_NOT_EXISTS);
        }
        projectDO.setProjectStatus(PjmsProjectStatusEnum.FINISH.getCode()).setFinishTime(LocalDateTime.now());
        projectMapper.updateById(projectDO);
    }

    @Override
    public void rollbackFinishManufacture(Long id) {
        ProjectDO projectDO = projectMapper.selectOne(ProjectDO::getId,id);
        if(projectDO == null){
            throw exception(PROJECT_NOT_EXISTS);
        }
        projectDO.setProjectStatus(PjmsProjectStatusEnum.PENDING_SUBMIT.getCode()).setFinishTime(null);
        projectMapper.updateById(projectDO);
    }

    @Override
    public void doneManufacture(ProjectDoneReqVO reqVo) {
        ProjectDO projectDO = projectMapper.selectOne(ProjectDO::getId,reqVo.getId());
        if(projectDO == null){
            throw exception(PROJECT_NOT_EXISTS);
        }
        if(reqVo.getDate() == null || reqVo.getDate().length < 2){
            throw exception(PROJECT_DATE_NOT_EXISTS);
        }
        projectDO.setStartDate(reqVo.getDate()[0]).setEndDate(reqVo.getDate()[1]);
        projectDO.setProjectStatus(PjmsProjectStatusEnum.DONE.getCode()).setDoneTime(LocalDateTime.now());
        projectMapper.updateById(projectDO);
    }

    @Override
    public PageResult<ProjectSimpleRespVO> getSimpleProjectPage(ProjectPageReqVO pageReqVO) {
        PageResult<ProjectDO> projectDOPageResult = projectMapper.selectPage(pageReqVO);
        List<ProjectDO> list = projectDOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return new PageResult<ProjectSimpleRespVO>().setList(null).setTotal((long)0);
        }
        List<ProjectSimpleRespVO> voList = BeanUtils.toBean(list, ProjectSimpleRespVO.class);
        return new PageResult<ProjectSimpleRespVO>().setList(voList).setTotal((long)voList.size());
    }

    @Override
    public ProjectDTO getProjectDTOById(Long id) {
        ProjectDO projectDO = validateProjectExists(id);
        return ProjectConvert.INSTANCE.convertProjectDTO(projectDO);
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public ProjectRespVO getProject( ProjectDetailReq projectDetailReq) {
        Long projectId = Objects.nonNull(projectDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(projectDetailReq.getProcessInstanceId()) : projectDetailReq.getProjectId();
        if (Objects.isNull(projectId)) {
            logger.error("[项目]未获取到项目id");
            return null;
        }
        ProjectDO projectDO = projectMapper.selectById(projectId);
        if (projectDO == null) {
            return null;
        }
        ProjectRespVO projectRespVO = ProjectConvert.INSTANCE.convertProjectRespVO(projectDO);
        projectRespVO.setPlanDate(new LocalDateTime[]{
                projectRespVO.getPlanStartDate(),projectRespVO.getPlanEndDate()
        });
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(projectDO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            projectRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        return projectRespVO;
    }

    @Override
    public PageResult<ProjectRespVO> getProjectPage(ProjectPageReqVO pageReqVO) {
        PageResult<ProjectDO> projectDOPageResult = projectMapper.selectPage(pageReqVO);


        List<ProjectDO> list = projectDOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return new PageResult<ProjectRespVO>().setList(null).setTotal((long)0);
        }
        List<ProjectRespVO> voList = BeanUtils.toBean(list, ProjectRespVO.class);
        voList.forEach(s->{
            s.setPlanDate(new LocalDateTime[]{
                    s.getPlanStartDate(),
                    s.getPlanEndDate()
            });
        });

        return new PageResult<ProjectRespVO>().setList(voList).setTotal((long)voList.size());
    }

}