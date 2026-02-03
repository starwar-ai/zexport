package com.syj.eplus.module.wms.controller.admin.warehouse.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 仓库管理-仓库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WarehouseSimpleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28362")
    private Long id;

    @Schema(description = "仓库编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("仓库编码")
    private String code;

    @Schema(description = "仓库名称", example = "李四")
    @ExcelProperty("仓库名称")
    private String name;

    @Schema(description = "仓管id")
    private Long managerId;

    @Schema(description = "仓管名称")
    private String managerName;

    @Schema(description = "仓管联系电话")
    private String managerMobile;

    @Schema(description = "默认标记")
    private String defaultFlag;
}
