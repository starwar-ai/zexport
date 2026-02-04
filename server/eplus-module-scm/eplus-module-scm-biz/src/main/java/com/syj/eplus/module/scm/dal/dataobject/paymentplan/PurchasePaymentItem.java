package com.syj.eplus.module.scm.dal.dataobject.paymentplan;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台  ")
@Data
@ExcelIgnoreUnannotated
public class PurchasePaymentItem {

    @Schema(description = "付款计划id")
    private Long paymentPlanId;
    @Schema(description = "出运单号")
    private String shipmentCode;

    @Schema(description = "起始日")
    private LocalDateTime startDate;
    @Schema(description = "实际付款日期")
    private LocalDateTime paymentDate;
    @Schema(description = "付款金额")
    private JsonAmount paymentAmount;
    @Schema(description = "付款人")
    private UserDept paymentUser;
    @Schema(description = "资金占用天数")
    private Long occupationDays;
    @Schema(description = "资金占用利息")
    private BigDecimal occupationInterest;

}