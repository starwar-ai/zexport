package cn.iocoder.yudao.module.bpm.api.task.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BpmTaskApproveReqDTO {

    @NotEmpty(message = "任务编号不能为空")
    private String id;

    private String code;

    //    @NotEmpty(message = "审批意见不能为空")
    private String reason;

    private List<String> ids;

}
