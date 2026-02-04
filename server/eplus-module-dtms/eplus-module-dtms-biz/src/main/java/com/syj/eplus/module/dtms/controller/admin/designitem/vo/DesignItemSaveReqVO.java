package com.syj.eplus.module.dtms.controller.admin.designitem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设计-认领明细新增/修改 Request VO")
@Data
public class DesignItemSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "设计任务单主键")
    private Long designId;

    @Schema(description = "认领人主键", example = "15376")
    private Long designerId;

    @Schema(description = "认领人姓名", example = "赵六")
    private String designerName;

    @Schema(description = "认领人部门主键", example = "32204")
    private Long designerDeptId;

    @Schema(description = "认领人部门名称", example = "张三")
    private String designerDeptName;

    @Schema(description = "计划开始时间")
    private LocalDateTime planStartDate;

    @Schema(description = "计划完成时间")
    private LocalDateTime planCompleteDate;

    @Schema(description = "设计任务类型1：常规任务，2：临时任务", example = "2")
    private Integer itemType;

    @Schema(description = "设计文件位置")
    private String designFilePath;

    @Schema(description = "异常说明")
    private String abnormalExplain;

    @Schema(description = "认领时间")
    private LocalDateTime acceptDate;

    @Schema(description = "完成时间")
    private LocalDateTime completeDate;

    @Schema(description = "评价结果；1：投诉，2：优秀，3：点赞")
    private Integer evaluateResult;

    @Schema(description = "评价描述")
    private String evaluateDesc;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}
