package cn.iocoder.yudao.module.system.api.notify.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 站内信发送给 Admin 或者 Member 用户
 *
 * @author xrcoder
 */
@Data
@Accessors(chain = true)
public class NotifySendSingleToUserReqDTO {

    /**
     * 用户编号
     */
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 站内信模板编号
     */
    @NotEmpty(message = "站内信模板编号不能为空")
    private String templateCode;

    /**
     * 站内信模板参数
     */
    private Map<String, Object> templateParams;

    /**
     * 业务类型
     * <p>
     * 例如：bpm_task（流程任务）, bpm_process（流程实例）等
     */
    private String businessType;

    /**
     * 业务ID
     * <p>
     * 例如：流程实例ID、任务ID等
     */
    private String businessId;

    /**
     * 业务参数
     * <p>
     * JSON格式，存储跳转所需的额外参数
     */
    private Map<String, Object> businessParams;
}
