package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/01/14:08
 * @Description:
 */
@Data
public class SimpleInvoicingNoticesReq {

    /**
     * 付款主体
     */
    private Long companyId;

    /**
     * 供应商编号
     */
    private String venderCode;
}
