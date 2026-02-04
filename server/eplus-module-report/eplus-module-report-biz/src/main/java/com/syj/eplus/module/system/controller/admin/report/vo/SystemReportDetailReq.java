package com.syj.eplus.module.system.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：模板详情请求封装实体(为区别不同入口)
 * @Author：chengbo
 * @Date：2024/1/20 19:48
 */
@Data
@Accessors(chain = true)
public class SystemReportDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "模板id", example = "1")
    private Long reportId;
}

