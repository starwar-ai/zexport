package cn.iocoder.yudao.module.bpm.api.task.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@ToString
public class BpmTaskRespDTO {
    /**
     * 审批人
     */
    private UserDept approver;
    /**
     * 审核时间
     */
    private LocalDateTime endTime;

    /**
     * 任务名字
     */
    private String name;

    /**
     * 审批结果
     */
    private Integer result;

    /**
     * 审批建议
     */
    private String reason = "-";
}
