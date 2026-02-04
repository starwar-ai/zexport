package com.syj.eplus.module.oa.controller.admin.subject.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 科目分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", example = "李四")
    private String name;

    @Schema(description = "描述", example = "随便")
    private String description;

    @Schema(description = "层次")
    private Integer layer;

    @Schema(description = "科目性质")
    private Integer nature;

    @Schema(description = "科目类型", example = "2")
    private String type;

    @Schema(description = "辅助核算")
    private String auxiliaryAccounting;

    @Schema(description = "核算编号")
    private String accountingNumber;

    @Schema(description = "是否外币核算")
    private Integer isForeignCurrencyAccounting;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否期末调汇")
    private Integer isFinalExchange;

    @Schema(description = "是否银行科目")
    private Integer isBank;

    @Schema(description = "是否现金科目")
    private Integer isCash;

    @Schema(description = "是否现金银行")
    private Integer isCashBank;

    @Schema(description = "父级科目id", example = "4249")
    private Long parentId;

    @Schema(description = "父级科目名称", example = "张三")
    private String parentName;

    @Schema(description = "录入日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inputDate;

    @Schema(description = "是否现金流量相关")
    private Integer isCashFlowRelated;

    @Schema(description = "是否末级")
    private Integer isLast;

    @Schema(description = "现金流量编号")
    private String cashFlowCode;

    @Schema(description = "现金流量名称", example = "赵六")
    private String cashFlowName;

    @Schema(description = "银行账户", example = "8840")
    private String bankAccount;

    @Schema(description = "银行账号")
    private String bankCode;

    @Schema(description = "余额方向")
    private Integer balanceDirection;

    @Schema(description = "科目余额")
    private Long balance;
}