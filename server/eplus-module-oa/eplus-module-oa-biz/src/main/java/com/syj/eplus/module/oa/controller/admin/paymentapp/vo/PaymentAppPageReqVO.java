package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 公对公申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentAppPageReqVO extends PageParam {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "申请单号")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "事由", example = "不对")
    private String reason;

    @Schema(description = "内部法人单位id", example = "30861")
    private Long companyId;

    @Schema(description = "支付对象")
    private Integer businessSubjectType;

    @Schema(description = "业务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("业务名称")
    private String businessSubjectName;

    @Schema(description = "业务编号")
    private String businessSubjectCode;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付币种")
    private String currency;

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "已支付金额")
    private BigDecimal paymentAmount;

    @Schema(description = "已支付币种")
    private String paymentCurrency;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] paymentDate;

    @Schema(description = "是否费用归属类型")
    private Integer feeShareFlag;

    private List<Integer>  businessSubjectTypeList;

    @Schema(description = "是否预付")
    private Integer prepaidFlag;

    @Schema(description = "创建人")
    private String creator;

    private Long deptId;

    private BigDecimal payAmountMin;

    private BigDecimal payAmountMax;

    private Integer linkFlag;

    private String paymentAppCode;


    private String remark;

}