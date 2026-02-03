package com.syj.eplus.module.dms.controller.admin.forwarderfee.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 船代费用 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ForwarderFeeShareFeeRespVO {

    @Schema(description = "出运明细主键")
    private Long shipmentId;

    @Schema(description = "出运明细编号")
    private String shipmentCode;

    @Schema(description = "发票号")
    private String code;

    @Schema(description = "归集金额")
    private JsonAmount amount;

    @Schema(description = "费用归集说明")
    private String desc;
    @Schema(description = "类型")
    private Integer businessType;

}