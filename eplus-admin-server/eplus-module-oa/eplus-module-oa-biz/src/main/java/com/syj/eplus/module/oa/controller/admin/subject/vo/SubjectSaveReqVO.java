package com.syj.eplus.module.oa.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 科目新增/修改 Request VO")
@Data
public class SubjectSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14769")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "名称不能为空")
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

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "币种不能为空")
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
    private LocalDateTime inputDate;

    @Schema(description = "是否现金流量相关")
    private Integer isCashFlowRelated;

    @Schema(description = "是否末级")
    private Integer isLast;

    @Schema(description = "现金流量编号")
    private String cashFlowCode;

    @Schema(description = "现金流量名称", example = "赵六")
    private String cashFlowName;

    @Schema(description = "银行账户", requiredMode = Schema.RequiredMode.REQUIRED, example = "8840")
    @NotEmpty(message = "银行账户不能为空")
    private String bankAccount;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "银行账号不能为空")
    private String bankCode;

    @Schema(description = "余额方向")
    private Integer balanceDirection;

    @Schema(description = "科目余额")
    private Long balance;
}