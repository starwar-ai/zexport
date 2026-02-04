package com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 报销还款详情 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReimbRepayDetailRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "661")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "报销单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29921")
    @ExcelProperty("报销单id")
    private Long reimbId;

    @Schema(description = "借款单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29108")
    @ExcelProperty("借款单id")
    private Long loanId;

    @Schema(description = "还款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("还款状态")
    private Integer repayStatus;

    @Schema(description = "还款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("还款金额")
    private JsonAmount repayAmount;

    @Schema(description = "还款日期", example = "2")
    @ExcelProperty("还款日期")
    private Integer auditStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "还款时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("还款时间")
    private LocalDateTime repayTime;

    @Schema(description = "还款来源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer repaySourceType;

    @Schema(description = "还款单号")
    private String repaySourceCode;

    @Schema(description = "还款人")
    private UserDept repayUser;
}