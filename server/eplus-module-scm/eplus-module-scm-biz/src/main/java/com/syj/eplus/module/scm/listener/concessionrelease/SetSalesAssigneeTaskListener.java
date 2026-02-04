package com.syj.eplus.module.scm.listener.concessionrelease;

import cn.hutool.extra.spring.SpringUtil;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.service.concessionrelease.ConcessionReleaseService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.Objects;

@Component
public class SetSalesAssigneeTaskListener implements TaskListener {
    @Serial
    private static final long serialVersionUID = 1L;

    public SetSalesAssigneeTaskListener() {
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        ConcessionReleaseService concessionReleaseService = SpringUtil.getBean(ConcessionReleaseService.class);
        // 获取业务员
        UserDept sales = concessionReleaseService.getAssigneeByInstanceId(processInstanceId);
        if (Objects.isNull(sales)) {
            return;
        }
        try {
            taskService.setAssignee(taskId, String.valueOf(sales.getUserId()));
            delegateTask.setAssignee(String.valueOf(sales.getUserId()));
        }catch (Exception e){

        }
    }
}