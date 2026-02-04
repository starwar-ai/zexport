package com.syj.eplus.module.dms.listener;

import cn.hutool.extra.spring.SpringUtil;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.DOCUMENTER_NOT_FUND;

public class SetDocumenterAssigneeTaskListener implements TaskListener {
    @Serial
    private static final long serialVersionUID = 1L;

    public SetDocumenterAssigneeTaskListener() {
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String taskId = delegateTask.getId();
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        Map<String, Object> variables = taskService.getVariables(taskId);
        Object documenterId = variables.get("documenterId");
        // 获取单证员
        if (Objects.isNull(documenterId)){
            throw exception(DOCUMENTER_NOT_FUND);
        }
        // 更新节点审批人
        taskService.setAssignee(taskId, String.valueOf(documenterId));
        delegateTask.setAssignee(String.valueOf(documenterId));
    }
}