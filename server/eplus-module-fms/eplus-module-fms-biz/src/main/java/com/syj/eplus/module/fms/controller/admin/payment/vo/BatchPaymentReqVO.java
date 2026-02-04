package com.syj.eplus.module.fms.controller.admin.payment.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "批量操作付款")
public class BatchPaymentReqVO {
    @Schema(description = "付款单编号")
    private Long paymentId;
    @Schema(description = "付款金额")
    private JsonAmount amount;
    @Schema(description = "付款日")
    private LocalDateTime paymentDate;

    @Schema(description = "付款单位")
    private Long companyId;

    @Schema(description = "付款银行")
    private String bank;

    @Schema(description = "付款方式")
    private Integer paymentMethod;
    @Schema(description = "银行账号")
    private String bankAccount;
}
