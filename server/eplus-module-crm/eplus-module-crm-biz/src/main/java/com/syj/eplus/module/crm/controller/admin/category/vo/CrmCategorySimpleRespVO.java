package com.syj.eplus.module.crm.controller.admin.category.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/6 17:53
 */

@Data
public class CrmCategorySimpleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28797")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String name;

    @Schema(description = "父节点id", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private Long parentId;

    @Schema(description = "父节点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String parentName;

    @Schema(description = "级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("级别")
    private Integer grade;

    @Schema(description = "子节点")
    private List<CrmCategorySimpleRespVO> children;
}
