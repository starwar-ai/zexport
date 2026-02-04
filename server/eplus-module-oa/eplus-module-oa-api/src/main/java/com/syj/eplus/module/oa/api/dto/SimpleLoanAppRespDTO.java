package com.syj.eplus.module.oa.api.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleLoanAppRespDTO {
    /**
     * ID
     */
    private Long id;

    /**
     * 借款单号
     */
    private String code;

    /**
     * 借款人
     */
    private UserDept applyer;
    /**
     * 借款金额
     */
    private JsonAmount amount;
    /**
     * 主体名称
     */
    private String companyName;
    /**
     * 主体id
     */
    private Long companyId;
    /**
     * 剩余未还款金额
     */
    private JsonAmount outstandingAmount;
    /**
     * 借款事由
     */
    private String purpose;

    /**
     * 已还金额
     */
    private JsonAmount repayAmount;

    /**
     * 还款中金额
     */
    private JsonAmount inRepaymentAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 借款申请日期
     */
    private LocalDateTime loanDate;

}
