package com.syj.eplus.module.crm.controller.admin.category.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 客户分类 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CrmCategoryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28797")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "客户分类编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户分类编号")
    private String code;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "流水号长度", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流水号长度")
    private Integer codeLen;

    @Schema(description = "父节点编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18523")
    @ExcelProperty("父节点编号")
    private Long parentId;

    @Schema(description = "种类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("种类")
    private Integer categoryType;

    @Schema(description = "级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("级别")
    private Integer grade;

    @Schema(description = "父节点名称")
    private String parentName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}