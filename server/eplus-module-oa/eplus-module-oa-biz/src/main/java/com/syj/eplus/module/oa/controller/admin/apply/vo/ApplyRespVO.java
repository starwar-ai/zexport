package com.syj.eplus.module.oa.controller.admin.apply.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - OA申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApplyRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25949")
    @ExcelProperty("id")
    private Long id;
    
    @Schema(description = "拟达成目标", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("拟达成目标")
    private String intendedObjectives;
    
    @Schema(description = "单据编号")
    @ExcelProperty("单据编号")
    private String code;
    
    @Schema(description = "企微申请单id", example = "21890")
    @ExcelProperty("企微申请单id")
    private String wecomId;

    @Schema(description = "申请人", example = "31322")
    @ExcelProperty("申请人")
    private UserDept applyer;

    @Schema(description = "申请时间")
    @ExcelProperty("申请时间")
    private LocalDateTime applyTime;
    
    @Schema(description = "出差事由")
    @ExcelProperty("出差事由")
    private String purpose;
    
    @Schema(description = "出差地点")
    @ExcelProperty("出差地点")
    private String dest;
    
    @Schema(description = "开始时间")
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "出差时长(天)")
    @ExcelProperty("出差时长(天)")
    private Integer duration;
    
    @Schema(description = "交通工具", example = "1")
    @ExcelProperty("交通工具")
    private Integer transportationType;
    
    @Schema(description = "同行人员")
    @ExcelProperty("同行人员")
    private List<UserDept> entertainEntourage;
    
    @Schema(description = "招待对象等级")
    @ExcelProperty("招待对象等级")
    private Integer entertainLevel;
    
    @Schema(description = "招待人数")
    @ExcelProperty("招待人数")
    private Integer entertainNum;
    
    @Schema(description = "招待日期")
    @ExcelProperty("招待日期")
    private LocalDateTime entertainTime;
    
    @Schema(description = "招待对象名称", example = "芋艿")
    @ExcelProperty("招待对象名称")
    private String entertainName;

    @Schema(description = "招待对象类型", example = "2")
    @ExcelProperty("招待对象类型")
    private Integer entertainType;
    
    @Schema(description = "一般费用名称")
    @ExcelProperty("一般费用名称")
    private String generalExpense;
    
    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remarks;
    
    @Schema(description = "是否申请报销")
    @ExcelProperty("是否申请报销")
    private Integer isApplyExpense;
    
    @Schema(description = "审核状态", example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;
    
    @Schema(description = "附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("附件")
    private List<SimpleFile> annex;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "申请单类型", example = "2")
    private Integer applyType;

    private JsonAmount amount;

    private String amountDesc;

    @Schema(description = "实际产生费用的人")
    private UserDept actualUser;

    private Long companyId;

    private String companyName;

    @Schema(description = "费用归集数据")
    private List<FeeShareRespDTO> feeShare;
    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "出差类型")
    private Integer travelType;

    @Schema(description = "申请报销次数")
    private Integer applyExpenseTimes;

}