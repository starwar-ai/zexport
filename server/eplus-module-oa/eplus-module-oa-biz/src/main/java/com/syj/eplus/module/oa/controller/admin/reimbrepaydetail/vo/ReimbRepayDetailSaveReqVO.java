package com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 报销还款详情新增/修改 Request VO")
@Data
public class ReimbRepayDetailSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "661")
    private Long id;

    @Schema(description = "报销单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29921")
    @NotNull(message = "报销单id不能为空")
    private Long reimbId;

    @Schema(description = "借款单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29108")
    @NotNull(message = "借款单id不能为空")
    private Long loanId;

    @Schema(description = "还款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2", defaultValue = "0")
    @NotNull(message = "还款状态不能为空")
    private Integer repayStatus = 0;

    @Schema(description = "还款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "还款金额不能为空")
    private JsonAmount repayAmount;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "币种", example = "CNY")
    private String currency;

    @Schema(description = "还款时间")
    private LocalDateTime repayTime;

    @Schema(description = "还款来源类型")
    private Integer repaySourceType;

    @Schema(description = "还款单号")
    private String repaySourceCode;

    @Schema(description = "还款人")
    private UserDept repayUser;

}