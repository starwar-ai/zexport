package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 公对公申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentAppRespFeeShareVO {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "归集金额")
    private JsonAmount amount;

    @Schema(description = "费用归集说明")
    private String desc;

}