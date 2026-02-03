package com.syj.eplus.module.oa.controller.admin.dictsubject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 类别配置新增/修改 Request VO")
@Data
public class DictSubjectSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21605")
    private Long id;

    @Schema(description = "科目id", example = "9323")
    private Long subjectId;

    @Schema(description = "科目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String subjectName;

    @Schema(description = "科目描述", example = "随便")
    private String subjectDescription;


    @Schema(description = "字典值类型")
    private List<String> systemDictTypeList;

    @Schema(description = "费用名称")
    private String feeName;

    @Schema(description = "费用描述")
    private String feeDesc;

    @Schema(description = "是否在描述展示")
    private Integer showDescFlag;

    @Schema(description = "是否在费用实际展示")
    private Integer showFeeFlag;
}