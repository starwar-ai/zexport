package com.syj.eplus.module.oa.controller.admin.repayapp.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.LocalDateTimeConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 还款单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RepayAppRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20456")
    private Long id;

    @Schema(description = "借款申请单id", example = "24122")
    private Long loanAppId;

    @Schema(description = "借款申请单编号", example = "24122")
    @ExcelProperty("借款申请单编号")
    private String loanAppCode;

    @Schema(description = "申请单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请单号")
    private String code;
    
    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印状态")
    private Integer printFlag;
    
    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印次数")
    private Integer printTimes;
    
    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;
    
    @Schema(description = "还款金额")
    @ExcelProperty(value = "还款金额", converter = AmountConvert.class)
    private JsonAmount amount;

    @Schema(description = "已转金额")
    @ExcelProperty(value = "已转金额", converter = AmountConvert.class)
    private JsonAmount repayAmount;

    @Schema(description = "借款金额")
    @ExcelProperty(value = "借款金额", converter = AmountConvert.class)
    private JsonAmount loanAmount;

    @Schema(description = "待还款金额")
    @ExcelProperty(value = "待还款金额", converter = AmountConvert.class)
    private JsonAmount outstandingAmount;

    @Schema(description = "还款中金额")
    private JsonAmount inRepaymentAmount;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;
    
    @Schema(description = "还款时间")
    @ExcelProperty(value = "还款时间", converter = LocalDateTimeConvert.class)
    private LocalDateTime repayTime;

    @Schema(description = "还款人", example = "2389")
    @ExcelProperty(value = "还款人", converter = UserDeptConverter.class)
    private UserDept repayer;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", converter = LocalDateTimeConvert.class)
    private LocalDateTime createTime;

    @Schema(description = "还款状态")
    private Integer repayStatus;

    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行账号")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行地址")
    @ExcelProperty("开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "还款方式")
    @ExcelProperty(value = "还款方式", converter = DictConvert.class)
    @DictFormat("loan_type")
    private Integer repayType;

    @Schema(description = "主体名称")
    private String companyName;

    @Schema(description = "主体编号")
    private Long companyId;

    @Schema(description = "任务id")
    private String processInstanceId;

    private String creator;

    @Schema(description = "附件")
    private List<SimpleFile> annex;
}