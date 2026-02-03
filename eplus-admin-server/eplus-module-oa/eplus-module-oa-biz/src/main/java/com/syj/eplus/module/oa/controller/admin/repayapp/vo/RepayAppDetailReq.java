package com.syj.eplus.module.oa.controller.admin.repayapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/19 11:11
 */
@Data
@Accessors(chain = true)
public class RepayAppDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "还款申请单id", example = "1")
    private Long repayAppId;

    @Schema(description = "还款申请单编号", example = "1")
    private String code;
}
