package com.syj.eplus.module.infra.controller.admin.formchange.vo;

import com.syj.eplus.module.infra.dal.dataobject.formchangeitem.FormChangeItemDO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 表单字段变更管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FormChangeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16353")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "描述")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("表名称")
    private String name;

    @Schema(description = "流程模型标识", example = "8511")
    @ExcelProperty("流程模型标识")
    private String modelKey;

    @Schema(description = "流程是否开启")
    @ExcelProperty("流程是否开启")
    private Integer instanceFlag;

    @Schema(description = "是否参与主流程")
    @ExcelProperty("是否参与主流程")
    private Integer mainInstanceFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "变更管理明细")
    private List<FormChangeItemDO> children;

}