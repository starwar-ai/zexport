package com.syj.eplus.module.system.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/1/7 12:40
 */
@Data
public class ReportInfoRespVO extends ReportRespVO {

    @Schema(description = "任务id")
    private String processInstanceId;

}
