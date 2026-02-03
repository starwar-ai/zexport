package com.syj.eplus.module.oa.api.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RepayAppDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 借款申请单id
     */
    private Long loanAppId;
    /**
     * 申请单号
     */
    private String code;
    /**
     * 打印状态
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 还款金额
     */
    private JsonAmount amount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 还款时间
     */
    private LocalDateTime repayTime;
    /**
     * 还款人id
     */
    private UserDept reparer;

    /**
     * 币种
     */
    private String currency;

    /**
     * 还款方式
     */
    private Integer repayType;
}
