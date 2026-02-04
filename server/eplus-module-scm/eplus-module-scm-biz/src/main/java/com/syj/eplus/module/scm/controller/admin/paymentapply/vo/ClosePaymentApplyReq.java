package com.syj.eplus.module.scm.controller.admin.paymentapply.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-08-08
 * @Description:
 */
@Data
public class ClosePaymentApplyReq {
    @Schema(description = "申请单id")
    private Long id;

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "作废原因")
    private String cancelReason;

    @Schema(description = "作废人")
    private UserDept cancelUser;

    @Schema(description = "业务类型")
    private Integer businessType;
}
