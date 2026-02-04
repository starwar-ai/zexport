package com.syj.eplus.module.oa.controller.admin.travelapp.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出差申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TravelAppRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5613")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "出差申请单编号", example = "12382")
    @ExcelProperty("出差申请单编号")
    private String code;

    @Schema(description = "企微申请单id", example = "12382")
    @ExcelProperty("企微申请单id")
    private String wecomId;

    @Schema(description = "申请人", example = "13690")
    @ExcelProperty("申请人")
    private Long applyerId;

    @Schema(description = "申请时间")
    @ExcelProperty("申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "出差事由")
    @ExcelProperty("出差事由")
    private String purpose;

    @Schema(description = "出差地点")
    @ExcelProperty("出差地点")
    private String dest;

    @Schema(description = "审核状态", example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "开始时间")
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "出差时长(秒)")
    @ExcelProperty("出差时长(秒)")
    private Long duration;

    @Schema(description = "交通工具", example = "1")
    @ExcelProperty("交通工具")
    private Integer transportationType;

    @Schema(description = "同行人员")
    @ExcelProperty("同行人员")
    private List<UserDept> companions;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "拟达成目标")
    private String intendedObjectives;
}