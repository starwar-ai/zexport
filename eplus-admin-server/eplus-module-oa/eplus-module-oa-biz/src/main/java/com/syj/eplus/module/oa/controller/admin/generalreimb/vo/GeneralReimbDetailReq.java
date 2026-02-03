package com.syj.eplus.module.oa.controller.admin.generalreimb.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/22 16:47
 */
@Data
public class GeneralReimbDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "报销单id", example = "1")
    private Long generalReimbId;
}
