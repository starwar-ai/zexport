package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/12/11:39
 * @Description:
 */
@Data
public class SimplePaymentAppResp {
    /**
     * id
     */
    private Long id;
    /**
     * 申请单号
     */
    private String code;
    /**
     * 内部法人单位id
     */
    private Long companyId;
    /**
     * 内部法人单位名称
     */
    private String companyName;
    /**
     * 支付对象
     */
    private Integer businessSubjectType;
    /**
     * 业务编号
     */
    private String businessSubjectCode;
    /**
     * 业务名称
     */
    private String businessSubjectName;

    /**
     * 金额
     */
    private JsonAmount amount;

    /**
     * 累计金额
     */
    private JsonAmount totalPaymentAmount;

    /**
     * 支付事由
     */
    private String reason;

    /**
     * 发票标识
     */
    private Integer invoiceFlag;

    /**
     * 支付金额
     */
    private JsonAmount payAmount;
}
