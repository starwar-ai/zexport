package com.syj.eplus.module.scm.listener.concessionrelease;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.syj.eplus.module.scm.service.concessionrelease.ConcessionReleaseService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.Set;

@Component
public class SetSaleManagerAssigneeTaskListener implements TaskListener {
    @Serial
    private static final long serialVersionUID = 1L;

    public SetSaleManagerAssigneeTaskListener() {
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        ConcessionReleaseService concessionReleaseService = SpringUtil.getBean(ConcessionReleaseService.class);
        // 获取业务员
        Set<Long> managerIdList = concessionReleaseService.getManagerAssigneeByInstanceId(processInstanceId);
        if (CollUtil.isEmpty(managerIdList)) {
            return;
        }
        try {
            managerIdList.stream().findFirst().ifPresent(s -> {
                // 更新节点审批人
                taskService.setAssignee(taskId, String.valueOf(s));
                delegateTask.setAssignee(String.valueOf(s));
            });
        }catch (Exception e){

        }

    }
}