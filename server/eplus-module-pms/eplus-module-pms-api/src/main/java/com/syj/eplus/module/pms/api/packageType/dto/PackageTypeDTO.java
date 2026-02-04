package com.syj.eplus.module.pms.api.packageType.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PackageTypeDTO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "英文名称")
    private String nameEng;

}
