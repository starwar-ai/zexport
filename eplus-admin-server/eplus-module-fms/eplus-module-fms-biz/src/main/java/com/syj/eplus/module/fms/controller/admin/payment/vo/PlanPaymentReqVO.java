package com.syj.eplus.module.fms.controller.admin.payment.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/15:43
 * @Description:
 */
@Data
@Schema(description = "计划付款请求体")
public class PlanPaymentReqVO {
    @Schema(description = "付款单主键")
    private Long paymentId;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "付款银行")
    private String bank;

    @Schema(description = "银行账号")
    private String bankAccount;

    private JsonAmount amount;

    @Schema(description = "承兑天数")
    private Integer acceptanceDays;

    @Schema(description = "附件")
    private List<SimpleFile> annex;
}
