package com.syj.eplus.module.fms.controller.admin.bankregistration.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 银行登记新增/修改 Request VO")
@Data
public class BankRegistrationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26006")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "登记人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept registeredBy;

    @Schema(description = "登记日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime registrationDate;

    @Schema(description = "入账单位id", example = "29053")
    private Long companyId;

    @Schema(description = "入账单位名称", example = "张三")
    private String companyName;

    @Schema(description = "公司抬头")
    private String companyTitle;

    @Schema(description = "银行入账日期")
    private LocalDateTime bankPostingDate;

    @Schema(description = "银行", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bank;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13961")
    private String bankAccount;

    @Schema(description = "开户行地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankAddress;

    @Schema(description = "开户行联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankPoc;

    @Schema(description = "银行行号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankCode;

    @Schema(description = "入账币别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "入账金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    private String remark;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String custName;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept manager;

    @Schema(description = "认领业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept claimManager;

    @Schema(description = "认领状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer claimStatus;

    @Schema(description = "认领日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime claimDate;

    @Schema(description = "关联外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String linkSaleContractCode;

    @Schema(description = "状态")
    private Integer status;

}