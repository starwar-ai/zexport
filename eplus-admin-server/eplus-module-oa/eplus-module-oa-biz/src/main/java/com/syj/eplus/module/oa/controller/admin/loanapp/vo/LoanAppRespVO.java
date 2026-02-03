package com.syj.eplus.module.oa.controller.admin.loanapp.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.LocalDateTimeConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 借款申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LoanAppRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19990")
    private Long id;

    @Schema(description = "单据编号")
    @ExcelProperty("单据编号")
    private String code;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "打印状态", converter = DictConvert.class)
    @DictFormat("print_flag") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer printFlag;

    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "借款事由")
    @ExcelProperty("借款事由")
    private String purpose;

    @Schema(description = "申请人")
    private UserDept applyer;

    @Schema(description = "借款金额")
    @ExcelProperty(value = "借款金额", converter = AmountConvert.class)
    private JsonAmount amount;

    @Schema(description = "借款申请日期")
    @ExcelProperty(value = "借款申请日期", converter = LocalDateTimeConvert.class)
    private LocalDateTime loanDate;

    @Schema(description = "借款方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "借款方式", converter = DictConvert.class)
    @DictFormat("loan_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer loanType;

    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行地址")
    @ExcelProperty("银行地址")
    private String bankAddress;

    @Schema(description = "银行账号", example = "5476")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "支付状态", example = "1")
    @ExcelProperty(value = "支付状态", converter = DictConvert.class)
    @DictFormat("loan_payment_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer paymentStatus;

    @Schema(description = "已转金额")
    @ExcelProperty(value = "已转金额", converter = AmountConvert.class)
    private JsonAmount paymentAmount;

    @Schema(description = "支付日期")
    @ExcelProperty("支付日期")
    private LocalDateTime paymentDate;

    @Schema(description = "还款状态", example = "2")
    @ExcelProperty(value = "还款状态", converter = DictConvert.class)
    @DictFormat("loan_repay_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer repayStatus;

    @Schema(description = "已还金额")
    @ExcelProperty(value = "已还金额", converter = AmountConvert.class)
    private JsonAmount repayAmount;

    @Schema(description = "实际还款日期")
    @ExcelProperty("实际还款日期")
    private LocalDateTime repayDate;

    @Schema(description = "剩余未还款金额")
    @ExcelProperty(value = "剩余未还款金额", converter = AmountConvert.class)
    private JsonAmount outstandingAmount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人编号")
    private String creator;

    @Schema(description = "创建人名称")
    private String creatorName;

    @Schema(description = "内部法人单位id")
    private Long companyId;

    @Schema(description = "内部法人单位名称")
    private String companyName;

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

    @Schema(description = "还款中金额")
    private JsonAmount inRepaymentAmount;

    @Schema(description = "汇率")
    private String exchangeRate;
}