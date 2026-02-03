package com.syj.eplus.module.oa.controller.admin.repayapp.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 还款单新增/修改 Request VO")
@Data
public class RepayAppSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20456")
    private Long id;

    @Schema(description = "借款申请单id", example = "24122")
    private Long loanAppId;

    @Schema(description = "申请单号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "申请单号不能为空")
    private String code;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印状态不能为空")
    private Integer printFlag;

    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印次数不能为空")
    private Integer printTimes;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "还款金额")
    private JsonAmount amount;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "还款时间")
    private LocalDateTime repayTime;

    @Schema(description = "还款人", example = "2389")
    private UserDept repayer;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "还款状态")
    private Integer repayStatus;
    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "还款方式")
    private Integer repayType;

    private Integer submitFlag;

    @Schema(description = "附件")
    private List<SimpleFile> annex;
}