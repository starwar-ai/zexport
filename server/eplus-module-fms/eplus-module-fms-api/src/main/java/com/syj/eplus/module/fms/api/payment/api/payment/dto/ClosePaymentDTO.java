package com.syj.eplus.module.fms.api.payment.api.payment.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-08-07
 * @Description: 作废付款单DTO
 */
@Data
public class ClosePaymentDTO {

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 业务编号
     */
    private String businessCode;

    /**
     * 作废时间
     */
    private LocalDateTime cancelTime;

    /**
     * 作废原因
     */
    private String cancelReason;

    /**
     * 作废人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cancelUser;
}
