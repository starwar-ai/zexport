package com.syj.eplus.module.infra.controller.admin.sn.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 序列号记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SnRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6470")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("类型")
    private String type;

    @Schema(description = "序列号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("序列号")
    private Integer sn;


    @Schema(description = "长度", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("长度")
    private Integer length;

}