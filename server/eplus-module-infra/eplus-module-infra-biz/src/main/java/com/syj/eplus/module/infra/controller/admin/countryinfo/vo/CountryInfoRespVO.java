package com.syj.eplus.module.infra.controller.admin.countryinfo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 国家信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CountryInfoRespVO {

    @Schema(description = "国家id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8029")
    @ExcelProperty("国家id")
    private Long id;

    @Schema(description = "国家名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("国家名称")
    private String name;

    @Schema(description = "国家编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("国家编码")
    private String code;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域编码")
    private String regionCode;

    @Schema(description = "区域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("区域名称")
    private String regionName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
