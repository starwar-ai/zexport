package com.syj.eplus.module.dms.controller.admin.commodityinspection.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 商检单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommodityInspectionPageReqVO extends PageParam {

    @Schema(description = "主体主键", example = "22523")
    private Long foreignTradeCompanyId;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "外销合同号")
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;
    @Schema(description = "客户名称")
    private String custName;
    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shipmentPlanCode;
}