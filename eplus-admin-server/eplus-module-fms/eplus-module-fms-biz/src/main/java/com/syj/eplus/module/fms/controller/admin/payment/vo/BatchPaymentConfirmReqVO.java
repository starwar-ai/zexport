package com.syj.eplus.module.fms.controller.admin.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/15:54
 * @Description:
 */
@Data
@Schema(description = "批量付款")
public class BatchPaymentConfirmReqVO {
    @Schema(description = "付款单编号列表")
    private List<SimplePayment> paymentIds;

    @Schema(description = "付款日")
    private LocalDateTime paymentDate;

    @Schema(description = "付款单位")
    private Long companyId;

    @Schema(description = "付款方式")
    private Integer paymentMethod;

    @Schema(description = "付款银行")
    private String bank;

    @Schema(description = "银行账号")
    private String bankAccount;
}
