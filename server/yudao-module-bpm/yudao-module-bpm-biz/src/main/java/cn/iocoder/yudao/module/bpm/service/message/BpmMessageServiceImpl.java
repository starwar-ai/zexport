package cn.iocoder.yudao.module.bpm.service.message;

import cn.iocoder.yudao.framework.web.config.WebProperties;
import cn.iocoder.yudao.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceApproveReqDTO;
import cn.iocoder.yudao.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceRejectReqDTO;
import cn.iocoder.yudao.module.bpm.service.message.dto.BpmMessageSendWhenTaskCreatedReqDTO;
import cn.iocoder.yudao.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.yudao.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * BPM 消息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class BpmMessageServiceImpl implements BpmMessageService {

    /**
     * 业务类型：流程任务
     */
    public static final String BUSINESS_TYPE_BPM_TASK = "bpm_task";

    /**
     * 站内信模板编码：任务分配通知
     */
    public static final String NOTIFY_TEMPLATE_TASK_ASSIGNED = "bpm_task_assigned";

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Resource
    private WebProperties webProperties;

    @Override
    public void sendMessageWhenProcessInstanceApprove(BpmMessageSendWhenProcessInstanceApproveReqDTO reqDTO) {
        // 短信功能已移除，可以在此添加其他通知方式
    }

    @Override
    public void sendMessageWhenProcessInstanceReject(BpmMessageSendWhenProcessInstanceRejectReqDTO reqDTO) {
        // 短信功能已移除，可以在此添加其他通知方式
    }

    @Override
    public void sendMessageWhenTaskAssigned(BpmMessageSendWhenTaskCreatedReqDTO reqDTO) {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("processInstanceName", reqDTO.getProcessInstanceName());
        templateParams.put("taskName", reqDTO.getTaskName());
        templateParams.put("startUserNickname", reqDTO.getStartUserNickname());
        templateParams.put("detailUrl", getProcessInstanceDetailUrl(reqDTO.getProcessInstanceId()));

        // 发送站内信通知（带业务关联，支持点击跳转）
        try {
            Map<String, Object> businessParams = new HashMap<>();
            businessParams.put("processInstanceId", reqDTO.getProcessInstanceId());
            businessParams.put("taskId", reqDTO.getTaskId());
            businessParams.put("taskName", reqDTO.getTaskName());

            NotifySendSingleToUserReqDTO notifyReqDTO = new NotifySendSingleToUserReqDTO()
                    .setUserId(reqDTO.getAssigneeUserId())
                    .setTemplateCode(NOTIFY_TEMPLATE_TASK_ASSIGNED)
                    .setTemplateParams(templateParams)
                    .setBusinessType(BUSINESS_TYPE_BPM_TASK)
                    .setBusinessId(reqDTO.getProcessInstanceId())
                    .setBusinessParams(businessParams);
            notifyMessageSendApi.sendSingleMessageToAdmin(notifyReqDTO);
        } catch (Exception e) {
            // 站内信发送失败不影响主流程
            log.warn("[sendMessageWhenTaskAssigned][发送站内信失败，流程实例ID({}), 任务ID({})]",
                    reqDTO.getProcessInstanceId(), reqDTO.getTaskId(), e);
        }
    }

    private String getProcessInstanceDetailUrl(String taskId) {
        return webProperties.getAdminUi().getUrl() + "/bpm/process-instance/detail?id=" + taskId;
    }

}
