package com.syj.eplus.module.fms.controller.admin.bankregistration.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 银行登记 Response VO")
@Data
@ExcelIgnoreUnannotated
public class BankRegistrationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26006")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "登记人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登记人")
    private UserDept registeredBy;
    
    @Schema(description = "登记日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登记日期")
    private LocalDateTime registrationDate;
    
    @Schema(description = "入账单位id", example = "29053")
    @ExcelProperty("入账单位id")
    private Long companyId;
    
    @Schema(description = "入账单位名称", example = "张三")
    @ExcelProperty("入账单位名称")
    private String companyName;
    
    @Schema(description = "公司抬头")
    @ExcelProperty("公司抬头")
    private String companyTitle;
    
    @Schema(description = "银行入账日期")
    @ExcelProperty("银行入账日期")
    private LocalDateTime bankPostingDate;
    
    @Schema(description = "银行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("银行")
    private String bank;
    
    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13961")
    @ExcelProperty("银行账号")
    private String bankAccount;
    
    @Schema(description = "开户行地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开户行地址")
    private String bankAddress;
    
    @Schema(description = "开户行联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开户行联系人")
    private String bankPoc;
    
    @Schema(description = "银行行号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("银行行号")
    private String bankCode;
    
    @Schema(description = "入账币别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入账币别")
    private String currency;
    
    @Schema(description = "入账金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入账金额")
    private BigDecimal amount;
    
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @ExcelProperty("备注")
    private String remark;
    
    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    private String custCode;
    
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("客户名称")
    private String custName;
    
    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("业务员")
    private UserDept manager;
    
    @Schema(description = "认领业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("认领业务员")
    private UserDept claimManager;
    
    @Schema(description = "认领状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("认领状态")
    private Integer claimStatus;
    
    @Schema(description = "认领日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("认领日期")
    private LocalDateTime claimDate;
    
    @Schema(description = "关联外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("关联外销合同号")
    private String linkSaleContractCode;

    @Schema(description = "已认领金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal claimedAmount;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "业务员列表")
    private List<UserDept> managerList;

    @Schema(description = "客户列表")
    private List<SimpleData> custList;
}