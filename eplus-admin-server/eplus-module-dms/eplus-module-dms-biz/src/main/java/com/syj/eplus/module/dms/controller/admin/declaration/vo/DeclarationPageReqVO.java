package com.syj.eplus.module.dms.controller.admin.declaration.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 报关单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeclarationPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "外销合同号")
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "编号")
    private String custName;

    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "计划单号")
    private String shipmentPlanCode;

    @Schema(description = "归属公司")
    private Long foreignTradeCompanyId;

    @Schema(description = "报关中文名称")
    private String declarationName;

    @Schema(description = "报关英文名称")
    private String customsDeclarationNameEng;
}