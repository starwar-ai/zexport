package com.syj.eplus.module.oa.controller.admin.loanapp.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 借款申请单新增/修改 Request VO")
@Data
public class LoanAppSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19990")
    private Long id;

    @Schema(description = "单据编号")
    private String code;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印状态不能为空")
    private Integer printFlag;

    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印次数不能为空")
    private Integer printTimes;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "借款事由")
    private String purpose;

    @Schema(description = "申请人id")
    private Long applyerId;

    @Schema(description = "借款金额")
    private JsonAmount amount;

    @Schema(description = "借款申请日期")
    private LocalDateTime loanDate;

    @Schema(description = "借款方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "借款方式不能为空")
    private Integer loanType;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行地址")
    private String bankAddress;

    @Schema(description = "银行账号", example = "5476")
    private String bankAccount;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "支付状态", example = "1")
    private Integer paymentStatus;

    @Schema(description = "已转金额")
    private JsonAmount paymentAmount;

    @Schema(description = "支付日期")
    private LocalDateTime paymentDate;

    @Schema(description = "还款状态", example = "2")
    private Integer repayStatus;

    @Schema(description = "已还金额")
    private JsonAmount repayAmount;

    @Schema(description = "实际还款日期")
    private LocalDateTime repayDate;

    @Schema(description = "剩余未还款金额")
    private JsonAmount outstandingAmount;

    private Integer submitFlag;

    @Schema(description = "内部法人单位")
    private Long companyId;

    @Schema(description = "出纳员")
    private UserDept cashier;

    @Schema(description = "借款类型")
    private Integer loanSource;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "借款状态")
    private Integer loanStatus;

    @Schema(description = "截止时间")
    private LocalDateTime deadlineTime;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "汇率")
    private String exchangeRate;

}