package com.syj.eplus.module.dms.controller.admin.settlementform.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 结汇单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SettlementFormPageReqVO extends PageParam {
    @Schema(description = "结汇单编号")
    private String code;
    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "客户PO号", example = "张三")
    private String custPo;

    @Schema(description = "出运单号")
    private String shipmentCode;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}