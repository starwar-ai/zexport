package com.syj.eplus.module.scm.api.paymentapply.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdatePaymentApplyDTO {
    private List<String> paymnetApplyCodes;
    private LocalDateTime paymentDate;
    private UserDept paymentUser;
    private String bank;
    private String bankAccount;
    private BigDecimal applyTotalAmount;
    private String currency;
}
