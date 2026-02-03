package cn.iocoder.yudao.module.bpm.service.definition;

import cn.iocoder.yudao.module.bpm.api.definition.dto.BpmNodeUser;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleCreateReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleRespVO;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.rule.BpmTaskAssignRuleUpdateReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.definition.BpmTaskAssignRuleDO;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * BPM 任务分配规则 Service 接口
 *
 * @author 芋道源码
 */
public interface BpmTaskAssignRuleService {

    /**
     * 获得流程定义的任务分配规则数组
     *
     * @param processDefinitionId 流程定义的编号
     * @param taskDefinitionKey   流程任务定义的 Key。允许空
     * @return 任务规则数组
     */
    List<BpmTaskAssignRuleDO> getTaskAssignRuleListByProcessDefinitionId(String processDefinitionId,
                                                                         @Nullable String taskDefinitionKey);

    /**
     * 获得流程模型的任务规则数组
     *
     * @param modelId 流程模型的编号
     * @return 任务规则数组
     */
    List<BpmTaskAssignRuleDO> getTaskAssignRuleListByModelId(String modelId);

    /**
     * 获得流程定义的任务分配规则数组
     *
     * @param modelId             流程模型的编号
     * @param processDefinitionId 流程定义的编号
     * @return 任务规则数组
     */
    List<BpmTaskAssignRuleRespVO> getTaskAssignRuleList(String modelId, String processDefinitionId);

    /**
     * 创建任务分配规则
     *
     * @param reqVO 创建信息
     * @return 规则编号
     */
    Long createTaskAssignRule(@Valid BpmTaskAssignRuleCreateReqVO reqVO);

    /**
     * 更新任务分配规则
     *
     * @param reqVO 创建信息
     */
    void updateTaskAssignRule(@Valid BpmTaskAssignRuleUpdateReqVO reqVO);

    /**
     * 判断指定流程模型和流程定义的分配规则是否相等
     *
     * @param modelId             流程模型编号
     * @param processDefinitionId 流程定义编号
     * @return 是否相等
     */
    boolean isTaskAssignRulesEquals(String modelId, String processDefinitionId);

    /**
     * 将流程流程模型的任务分配规则，复制一份给流程定义
     * 目的：每次流程模型部署时，都会生成一个新的流程定义，此时考虑到每次部署的流程不可变性，所以需要复制一份给该流程定义
     *
     * @param fromModelId           流程模型编号
     * @param toProcessDefinitionId 流程定义编号
     */
    void copyTaskAssignRules(String fromModelId, String toProcessDefinitionId);

    /**
     * 校验流程模型的任务分配规则全部都配置了
     * 目的：如果有规则未配置，会导致流程任务找不到负责人，进而流程无法进行下去！
     *
     * @param id 流程模型编号
     */
    void checkTaskAssignRuleAllConfig(String id);

    /**
     * 计算当前执行任务的处理人
     *
     * @param execution 执行任务
     * @return 处理人的编号数组
     */
    Set<Long> calculateTaskCandidateUsers(DelegateExecution execution);

    /**
     * 根据用户编号获取部门负责人编号
     *
     * @param userId 用户编号
     * @return 部门负责人编号
     */
    Long getLeaderIdByUserId(Long userId);

    /**
     * @param processInstanceId
     * @return
     */
    Long getCreatorByProcessInstanceId(String processInstanceId);

    /**
     * 获得指令流程实例的流程任务列表，包括所有状态的
     *
     * @param processInstanceId 流程实例的编号
     * @return 流程任务列表
     */
    List<BpmTaskRespVO> getTaskListByProcessInstanceId(String processInstanceId);

    /**
     * 处理相同节点审批人任务
     *
     * @param taskId            任务ID
     * @param processInstanceId 流程实例编号
     * @param assigneeId        当前节点审批人
     */
    void dealSimpleTask(String taskId, String processInstanceId, Long assigneeId);

    /**
     * 校验发起人在审批节点是否需要交给上级领导审批
     *
     * @param processDefinitionId 流程定义编号
     * @return 是否需要
     */
    boolean checkTransferFlag(String processDefinitionId);


    /**
     * 根据模型标识跟流程实例id获取所有节点审批人信息
     * @param modelKey 模型标识
     * @param processInstanceId 流程实例id
     * @return 所有节点审批人信息
     */
    List<BpmNodeUser> getAllAssigneeByModuleId(String modelKey, String processInstanceId);

    /**
     * 验证流程实例是否需要自动审核通过
     * @param processInstanceId 流程实例id
     * @return 是否需要自动审核通过
     */
    boolean validateAutoApproveByProcessInstanceId(String processInstanceId);


    /**
     * 自动通过流程任务
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程模型id
     * @param assigneeUserId 审批人
     */
    void autoComplete(String taskId,String processInstanceId,String processDefinitionId,Long assigneeUserId);
}
