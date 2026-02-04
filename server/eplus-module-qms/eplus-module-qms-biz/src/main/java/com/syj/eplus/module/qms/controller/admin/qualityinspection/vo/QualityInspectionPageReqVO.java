package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 验货单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QualityInspectionPageReqVO extends PageParam {

    @Schema(description = "1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案", example = "1")
    private List<Integer> qualityInspectionStatus;

    @Schema(description = "单号")
    private String code;

    @Schema(description = "录入日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "验货日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inspectionTime;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "供应商id", example = "11989")
    private Long venderId;

    @Schema(description = "供应商code", example = "11989")
    private Long venderCode;

    @Schema(description = "申请验货人", example = "20824")
    private Long applyInspectorId;

    @Schema(description = "验货人", example = "26453")
    private Long inspectorId;

    @Schema(description = "验货方式:  1： 泛太陪验（工厂）" +
            "2：泛太陪验（公司内）\n" +
            "3：泛太自验（工厂）\n" +
            "4：泛太自验（公司内）\n" +
            "5：客户自检\n" +
            "6：客户指定第三方\n" +
            "7：远程验货", example = " 1")
    private Integer inspectionType;

    @Schema(description = "是否树形结构")
    private Boolean isTree;
}
