package com.syj.eplus.module.oa.controller.admin.loanapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：借款申请单详情请求封装实体(为区别不同入口)
 * @Author：chengbo
 * @Date：2024/1/31 09:48
 */
@Data
@Accessors(chain = true)
public class LoanAppDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "借款申请单id", example = "1")
    private Long loanAppId;
}
