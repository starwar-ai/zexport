package com.syj.eplus.module.crm.controller.admin.cust.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：客户详情请求封装实体(为区别不同入口)
 * @Author：du
 * @Date：2024/1/20 19:48
 */
@Data
@Accessors(chain = true)
public class CrmCustDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "客户id", example = "1")
    private Long custId;

    @Schema(description = "客户编码", example = "1")
    private String custCode;
}

