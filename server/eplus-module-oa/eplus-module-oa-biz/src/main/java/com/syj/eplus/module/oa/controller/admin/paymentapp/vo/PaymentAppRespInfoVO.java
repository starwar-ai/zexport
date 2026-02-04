package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 公对公申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentAppRespInfoVO extends PaymentAppRespVO  {

    @Schema(description = "借款单列表")
    private List<LoanDescVO> loanList;

    @Schema(description = "借款单汇总")
    private List<LoanSumVO> locanSumList;

}