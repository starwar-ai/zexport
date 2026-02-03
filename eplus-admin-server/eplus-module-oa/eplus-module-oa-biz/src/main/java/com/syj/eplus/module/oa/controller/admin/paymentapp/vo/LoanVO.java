package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 借款单明细 VO")
@Data
@ExcelIgnoreUnannotated
public class LoanVO {

    @Schema(description = "明细列表")
    private List<LoanDescVO> loanDescList;

    @Schema(description = "汇总列表")
    private List<LoanSumVO> loanSumList;

}