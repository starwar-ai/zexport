package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 借款单明细 VO")
@Data
@ExcelIgnoreUnannotated
public class LoanSumVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "是否存在")
    private Integer exitFlag;


}