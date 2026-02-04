package com.syj.eplus.module.oa.controller.admin.feesharedesc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 费用归集具体名称新增/修改 Request VO")
@Data
public class FeeShareDescItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15141")
    private Long id;


    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "序号", example = "1")
    private Integer sortNum;
}