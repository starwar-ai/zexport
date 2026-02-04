package com.syj.eplus.module.crm.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 客户分类新增/修改 Request VO")
@Data
public class CrmCategorySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28797")
    private Long id;

    @Schema(description = "客户分类编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String name;

    @Schema(description = "流水号长度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer codeLen;

    @Schema(description = "父节点编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18523")
    private Long parentId;

    @Schema(description = "种类", requiredMode = Schema.RequiredMode.REQUIRED, example = "18523")
    private Integer categoryType;

    @Schema(description = "级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "18523")
    private Integer grade;
}