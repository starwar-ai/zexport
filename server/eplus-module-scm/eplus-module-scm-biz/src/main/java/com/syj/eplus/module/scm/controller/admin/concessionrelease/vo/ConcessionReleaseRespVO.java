package com.syj.eplus.module.scm.controller.admin.concessionrelease.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 让步放行 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConcessionReleaseRespVO extends QualityInspectionRespDTO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7876")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "验货单主键 ", example = "30244")
    @ExcelProperty("验货单主键 ")
    private Long qualityInspectionId;

//    @Schema(description = "验货单主键 ")
//    private QualityInspectionRespDTO qualityInspection;
    
    @Schema(description = "保函标记", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("保函标记")
    private Integer annexFlag;
    
    @Schema(description = "保函", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("保函")
    private List<SimpleFile>  concessionReleaseAnnex;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("图片")
    private List<SimpleFile>  picture;

    @Schema(description = "审核状态", example = "2")
    @ExcelProperty("审核状态")
    private Integer auditStatus;
    
    @Schema(description = "让步描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("让步描述")
    private String description;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "流程实例id",type = "UUID")
    private String processInstanceId;
    
}