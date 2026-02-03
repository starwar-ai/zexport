package com.syj.eplus.module.fms.controller.admin.payment.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 财务付款表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentPageReqVO extends PageParam {

    @Schema(description = "支付编码")
    private String code;

    @Schema(description = "内部法人单位", example = "13149")
    private Long companyId;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行账号", example = "538")
    private String bankAccount;

    @Schema(description = "开户行地址", example = "张三")
    private String bankAddress;

    @Schema(description = "支付状态", example = "1")
    private List<Integer> status;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] date;

    @Schema(description = "出纳员编号", example = "22506")
    private Long cashierId;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "业务类型", example = "2")
    private Integer businessType;

    @Schema(description = "业务id", example = "30558")
    private String businessCode;

    @Schema(description = "申请部门", example = "7331")
    private Long applyerDeptId;

    @Schema(description = "申请人id", example = "12015")
    private Long applyerId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "支付币种")
    private String currency;

    @Schema(description = "申请支付金额")
    private BigDecimal applyAmount;

    @Schema(description = "最终审批人")
    private UserDept approver;

    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "申请人主键", example = "31322")
    private Long applierId;
}