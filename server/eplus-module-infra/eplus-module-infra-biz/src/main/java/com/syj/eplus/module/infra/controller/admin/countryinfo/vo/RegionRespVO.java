package com.syj.eplus.module.infra.controller.admin.countryinfo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 地区 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = true)
public class RegionRespVO {

    @Schema(description = "区域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("区域名称")
    private String name;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域编码")
    private String code;

}
