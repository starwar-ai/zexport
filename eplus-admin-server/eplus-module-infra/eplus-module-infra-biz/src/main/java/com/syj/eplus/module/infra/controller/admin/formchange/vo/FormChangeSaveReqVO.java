package com.syj.eplus.module.infra.controller.admin.formchange.vo;

import com.syj.eplus.module.infra.dal.dataobject.formchangeitem.FormChangeItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 表单字段变更管理新增/修改 Request VO")
@Data
public class FormChangeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16353")
    private Long id;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "表名称不能为空")
    private String name;

    @Schema(description = "流程模型标识", example = "8511")
    private String modelKey;

    @Schema(description = "流程是否开启")
    private Integer instanceFlag;

    @Schema(description = "是否参与主流程")
    private Integer mainInstanceFlag;

    @Schema(description = "变更管理明细")
    private List<FormChangeItemDO> children;

}