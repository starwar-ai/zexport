package cn.iocoder.yudao.module.bpm.framework.flowable.core.behavior;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.module.bpm.service.definition.BpmTaskAssignRuleService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;
import java.util.Set;

/**
 * 自定义的【单个】流程任务的 assignee 负责人的分配
 * 第一步，基于分配规则，计算出分配任务的【单个】候选人。如果找不到，则直接报业务异常，不继续执行后续的流程；
 * 第二步，随机选择一个候选人，则选择作为 assignee 负责人。
 *
 * @author 芋道源码
 */
@Slf4j
public class BpmUserTaskActivityBehavior extends UserTaskActivityBehavior {

    @Setter
    private BpmTaskAssignRuleService bpmTaskRuleService;

    public BpmUserTaskActivityBehavior(UserTask userTask) {
        super(userTask);
    }

    @Override
    protected void handleAssignments(TaskService taskService, String assignee, String owner,
                                     List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, ExpressionManager expressionManager,
                                     DelegateExecution execution, ProcessEngineConfigurationImpl processEngineConfiguration) {
        String processDefinitionId = execution.getProcessDefinitionId();
        String processInstanceId = task.getProcessInstanceId();
        // 第一步，获得任务的候选用户
        Set<Long> candidateUserIds = calculateTaskCandidateUsers(execution);
        // 未获取到任务审批人则判断是否需要自动通过 否则抛出异常
//        if (CollUtil.isEmpty(candidateUserIds)) {
//            boolean autoApproveFlag = bpmTaskRuleService.validateAutoApproveByProcessInstanceId(processInstanceId);
//            if (autoApproveFlag){
//                TaskHelper.changeTaskAssignee(task, String.valueOf(loginUserId));
//                bpmTaskRuleService.autoComplete(task.getId(),processInstanceId,processDefinitionId,loginUserId);
//                return;
//            }else {
//                throw exception(TASK_CREATE_FAIL_NO_CANDIDATE_USER);
//            }
//        }

        Long startUserId = bpmTaskRuleService.getCreatorByProcessInstanceId(processInstanceId);
        boolean transferFlag = bpmTaskRuleService.checkTransferFlag(processDefinitionId);
        // 如果当前审批人在审批节点上则交给部门负责人审批
        if (transferFlag && candidateUserIds.contains(startUserId)) {
            Long leaderId = bpmTaskRuleService.getLeaderIdByUserId(startUserId);
            TaskHelper.changeTaskAssignee(task, String.valueOf(leaderId));
            bpmTaskRuleService.dealSimpleTask(task.getId(), processInstanceId, leaderId);
            return;
        }
        int index = RandomUtil.randomInt(candidateUserIds.size());
        Long assigneeUserId = CollUtil.get(candidateUserIds, index);
        Assert.notNull(assigneeUserId, "任务处理人不能为空");
        // 第二步，设置作为负责人
        TaskHelper.changeTaskAssignee(task, String.valueOf(assigneeUserId));
        bpmTaskRuleService.dealSimpleTask(task.getId(), processInstanceId, assigneeUserId);
    }

    private Set<Long> calculateTaskCandidateUsers(DelegateExecution execution) {
        // 情况一，如是多实例的任务果，例如说会签、或签等情况，则从 Variable 中获取。它的任务处理人在 BpmParallelMultiInstanceBehavior 中已经被分配了
        if (super.multiInstanceActivityBehavior != null) {
            String collectionElementVariable = super.multiInstanceActivityBehavior.getCollectionElementVariable();
            FlowElement element = execution.getCurrentFlowElement();
            if (element instanceof EndEvent) {
                element = ((ExecutionEntityImpl) execution).getOriginatingCurrentFlowElement();
                execution.setCurrentFlowElement(element);
            }
            Long variable = execution.getVariable(collectionElementVariable, Long.class);
            return Set.of(variable);
        }
        // 情况二，如果非多实例的任务，则计算任务处理人
        // 第一步，先计算可处理该任务的处理人们
        return bpmTaskRuleService.calculateTaskCandidateUsers(execution);
        // 第二步，后随机选择一个任务的处理人
        // 疑问：为什么一定要选择一个任务处理人？
        // 解答：项目对 bpm 的任务是责任到人，所以每个任务有且仅有一个处理人。
        //      如果希望一个任务可以同时被多个人处理，可以考虑使用 BpmParallelMultiInstanceBehavior 实现的会签 or 或签。
        //TODO
    }


}
