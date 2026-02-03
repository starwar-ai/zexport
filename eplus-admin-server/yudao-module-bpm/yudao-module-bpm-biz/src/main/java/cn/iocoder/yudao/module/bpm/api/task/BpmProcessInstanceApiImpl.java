package cn.iocoder.yudao.module.bpm.api.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRespDTO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCancelReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskApproveReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRejectReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskTransferReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceExtDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmTaskExtDO;
import cn.iocoder.yudao.module.bpm.enums.DictTypeConstants;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.bpm.service.task.BpmProcessInstanceService;
import cn.iocoder.yudao.module.bpm.service.task.BpmTaskService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.OperateLogFormatDict;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.entity.UserDept;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Flowable 流程实例 Api 实现类
 *
 * @author 芋道源码
 * @author jason
 */
@Service
@Validated
public class BpmProcessInstanceApiImpl implements BpmProcessInstanceApi {

    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmTaskService bpmTaskService;

    @Resource
    private AdminUserApi adminUserApi;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String createProcessInstance(Long userId, String processKey, Long auditableId) {
        BpmProcessInstanceCreateReqDTO bpmProcessInstanceCreateReqDTO = new BpmProcessInstanceCreateReqDTO()
                .setProcessDefinitionKey(processKey)
                .setAuditAbleId(auditableId)
                .setBusinessKey(String.valueOf(auditableId));
        String processInstanceId = processInstanceService.createProcessInstance(userId, bpmProcessInstanceCreateReqDTO);
        // TODO 如果申请人在节点上 将改流程转为领导审批

        return processInstanceId;
    }

    @Override
    public String createProcessInstance(Long userId, String processKey, String businessKey, Long auditableId) {
        BpmProcessInstanceCreateReqDTO bpmProcessInstanceCreateReqDTO = new BpmProcessInstanceCreateReqDTO()
                .setProcessDefinitionKey(processKey)
                .setAuditAbleId(auditableId)
                .setBusinessKey(businessKey);
        return processInstanceService.createProcessInstance(userId, bpmProcessInstanceCreateReqDTO);
    }

    @Override
    public String createProcessInstance(Long userId, String processKey, Long auditableId, Map<String, Object> variables,Map<String,List<Long>> assignee) {
        BpmProcessInstanceCreateReqDTO bpmProcessInstanceCreateReqDTO = new BpmProcessInstanceCreateReqDTO()
                .setProcessDefinitionKey(processKey)
                .setAuditAbleId(auditableId)
                .setBusinessKey(String.valueOf(auditableId));
        if (CollUtil.isNotEmpty(variables)){
            bpmProcessInstanceCreateReqDTO.setVariables(variables);
        }
        if (CollUtil.isNotEmpty(assignee)){
            bpmProcessInstanceCreateReqDTO.setAssignee(assignee);
        }
        return processInstanceService.createProcessInstance(userId, bpmProcessInstanceCreateReqDTO);
    }

    /**
     * 根据流程标识跟业务id获取当前审批任务列表
     *
     * @param definitionKey 流程标识
     * @param auditableId   业务id
     * @return 审批任务列表
     */
    @Override
    public String getBpmProcessInstanceId(Long auditableId, String definitionKey) {
        BpmProcessInstanceExtDO bpmProcessInstanceExtDO = processInstanceService.getProcessInstanceExtByAuditAbleId(auditableId, definitionKey);
        if (Objects.isNull(bpmProcessInstanceExtDO)) {
            logger.warn("[BpmProcessInstanceApiImpl]根据auditableId-{},definitionKey-{}未找到流程实例拓展", auditableId, definitionKey);
            return null;
        }
        return bpmProcessInstanceExtDO.getProcessInstanceId();
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmTaskService.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqVO.class));
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO, String operatorExtsKey) {
        bpmTaskService.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqVO.class));
        createOperateLog(reqDTO.getId(), reqDTO.getCode(), reqDTO.getReason(), operatorExtsKey);
    }

    private void createOperateLog(String id, String code, String reason, String operatorExtsKey) {
        BpmTaskRespDTO bpmTask = getBpmTask(id);
        if (Objects.nonNull(bpmTask)) {
            List<ChangeRecord> changeRecords = new ArrayList<>();
            changeRecords.add(new ChangeRecord().setFieldName(OperateLogFormatDict.APPROVE_NAME).setValue(code));
            String dictDataLabel = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.PROCESS_INSTANCE_RESULT, bpmTask.getResult());
            changeRecords.add(new ChangeRecord().setFieldName(OperateLogFormatDict.APPROVE_RESULT).setValue(dictDataLabel));
            changeRecords.add(new ChangeRecord().setFieldName(OperateLogFormatDict.APPROVE_NODE).setValue(bpmTask.getName()));
            changeRecords.add(new ChangeRecord().setFieldName(OperateLogFormatDict.APPROVE_OPINION).setValue(reason));
            changeRecords.add(new ChangeRecord().setFieldName(OperateLogFormatDict.APPROVE_USER).setValue(bpmTask.getApprover().getNickname()));
            OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
            AtomicReference<String> content = new AtomicReference<>(null);
            changeRecords.forEach(s -> {
                content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), null, s.getValue(), OperateTypeEnum.OTHER.getType()));
            });
            OperateLogUtils.setContent(content.get());
            OperateLogUtils.addExt(operatorExtsKey, code);
        }
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmTaskService.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqVO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO, String operatorExtsKey) {
        bpmTaskService.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqVO.class));
        createOperateLog(reqDTO.getId(), reqDTO.getCode(), reqDTO.getReason(), operatorExtsKey);
    }

    //    @Override
//    public BpmTaskRespDTO getBpmTask(String processInstanceId) {
//        List<BpmTaskRespVO> taskListByProcessInstance = bpmTaskService.getTaskListByProcessInstanceId(processInstanceId);
//        if (CollUtil.isNotEmpty(taskListByProcessInstance)) {
//            Optional<BpmTaskRespVO> first = taskListByProcessInstance.stream().filter(s -> BpmProcessInstanceResultEnum.APPROVE.getResult().equals(s.getResult())).findFirst();
//            if (first.isPresent()) {
//                BpmTaskRespVO bpmTaskRespVO = first.get();
//                return new BpmTaskRespDTO().setApprover(BeanUtils.toBean(bpmTaskRespVO.getAssigneeUser(), UserDept.class)).setEndTime(bpmTaskRespVO.getEndTime()).setName(bpmTaskRespVO.getName()).setResult(bpmTaskRespVO.getResult());
//            }
//        }
//        return new BpmTaskRespDTO();
//    }
    @Override
    public BpmTaskRespDTO getBpmTask(String processInstanceId) {
        BpmTaskExtDO taskExt = bpmTaskService.getTaskExtListByBpmProcessInstanceId(processInstanceId);
        if (Objects.nonNull(taskExt)) {
            Long assigneeUserId = taskExt.getAssigneeUserId();
            UserDept userDept = adminUserApi.getUserDeptByUserId(assigneeUserId);
            return new BpmTaskRespDTO().setApprover(userDept).setEndTime(taskExt.getEndTime()).setName(taskExt.getName()).setResult(taskExt.getResult()).setReason(taskExt.getReason());
        }
        return new BpmTaskRespDTO();
    }

    @Override
    public Long selectAuditAbleIdByProcessInstanceId(String processInstanceId) {
        return processInstanceService.selectAuditAbleIdByProcessInstanceId(processInstanceId);
    }

    @Override
    public Long getStartUserIdByProcessInstanceId(String processInstanceId) {
        List<BpmTaskRespVO> taskListByProcessInstanceId = bpmTaskService.getTaskListByProcessInstanceId(processInstanceId);
        if (CollUtil.isEmpty(taskListByProcessInstanceId)) {
            return null;
        }
        return taskListByProcessInstanceId.get(0).getProcessInstance().getStartUserId();
    }

    @Override
    public void transferTask(Long userId, String processInstanceId, Long assigneeUserId) {
        bpmTaskService.transferTask(userId, new BpmTaskTransferReqVO().setId(processInstanceId).setAssigneeUserId(assigneeUserId));
    }

    @Override
    public Map<String, Object> getVariablesByProcessInstanceId(String processInstanceId) {
        ProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        if (Objects.isNull(processInstance)) {
            return Map.of();
        }
        return processInstance.getProcessVariables();
    }

    @Override
    public void cancelProcessInstance(Long userId,String processInstanceId) {
        processInstanceService.cancelProcessInstance(userId,new BpmProcessInstanceCancelReqVO().setId(processInstanceId).setReason(CommonDict.BASE_SNAKE));
    }

    @Override
    public Map<String, String> getApproverByProcessInstance(String processInstanceId) {
        if (StrUtil.isEmpty(processInstanceId)){
            return Map.of();
        }
        List<BpmTaskRespVO> taskList = bpmTaskService.getTaskListByProcessInstanceId(processInstanceId);
        if (CollUtil.isEmpty(taskList)){
            return Map.of();
        }
        return taskList.stream().filter(s-> BpmProcessInstanceResultEnum.APPROVE.getResult().equals(s.getResult())).collect(Collectors.toMap(BpmTaskRespVO::getName, s -> s.getAssigneeUser().getNickname(),(o, n)->o));
    }
}