package com.syj.eplus.module.scm.listener.concessionrelease;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.io.Serial;

@Component
public class SetPurcahseManagerAssigneeTaskListener implements TaskListener {
    @Serial
    private static final long serialVersionUID = 1L;

    public SetPurcahseManagerAssigneeTaskListener() {
    }

    @Override
    public void notify(DelegateTask delegateTask) {
//        String processInstanceId = delegateTask.getProcessInstanceId();
//        String taskId = delegateTask.getId();
//        TaskService taskService = SpringUtil.getBean(TaskService.class);
//        ConcessionReleaseService concessionReleaseService = SpringUtil.getBean(ConcessionReleaseService.class);
//        // 获取业务员
//        Set<Long> managerIdList = concessionReleaseService.getPurchaseManagerAssigneeByInstanceId(processInstanceId);
//        if (CollUtil.isEmpty(managerIdList)) {
//            return;
//        }
//        managerIdList.stream().findFirst().ifPresent(s -> {
//            // 更新节点审批人
//            taskService.setAssignee(taskId, String.valueOf(s));
//            delegateTask.setAssignee(String.valueOf(s));
//        });
    }
}