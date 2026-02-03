package com.syj.eplus.module.fms.controller.admin.payment.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/20/17:04
 * @Description:
 */
@Data
public class SimplePayment {
    private Long id;

    private JsonAmount amount;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

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
