package com.syj.eplus.module.dtms.controller.admin.designitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设计-认领明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DesignItemPageReqVO extends PageParam {

    @Schema(description = "设计任务单主键", example = "9353")
    private Long designId;

    @Schema(description = "认领人主键", example = "15376")
    private Long designerId;

    @Schema(description = "认领人姓名", example = "赵六")
    private String designerName;

    @Schema(description = "认领人部门主键", example = "32204")
    private Long designerDeptId;

    @Schema(description = "认领人部门名称", example = "张三")
    private String designerDeptName;

    @Schema(description = "设计任务类型1：常规任务，2：临时任务", example = "2")
    private Integer itemType;

    @Schema(description = "设计文件位置")
    private String designFilePath;

    @Schema(description = "认领时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] acceptDate;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] completeDate;

    @Schema(description = "评价结果；1：投诉，2：优秀，3：点赞")
    private Integer evaluateResult;

    @Schema(description = "评价描述")
    private String evaluateDesc;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
