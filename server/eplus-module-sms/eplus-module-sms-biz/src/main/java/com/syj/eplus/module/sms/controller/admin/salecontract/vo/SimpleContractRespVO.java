package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/13/16:52
 * @Description:
 */
@Data
@Schema(description = "销售合同精简列表 - 调拨")
public class SimpleContractRespVO {
    @Schema(description = "编号")
    private String code;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;
}
