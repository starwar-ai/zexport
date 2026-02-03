package com.syj.eplus.module.oa.controller.admin.apply.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - OA申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplyPageReqVO extends PageParam {

    @Schema(description = "拟达成目标")
    private String intendedObjectives;

    @Schema(description = "单据编号")
    private String code;

    @Schema(description = "企微申请单id", example = "21890")
    private String wecomId;

    @Schema(description = "申请人主键", example = "31322")
    private Long applierId;
    @Schema(description = "申请人部门主键", example = "31322")
    private Long applierDeptId;

    private String applierDeptName;
    @Schema(description = "申请时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyTime;

    @Schema(description = "出差事由")
    private String purpose;

    @Schema(description = "出差地点")
    private String dest;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "出差时长(秒)")
    private Integer duration;

    @Schema(description = "交通工具", example = "1")
    private Integer transportationType;

    @Schema(description = "同行人员")
    private List<UserDept> entertainEntourage;

    @Schema(description = "招待对象等级")
    private String entertainLevel;

    @Schema(description = "招待人数")
    private Integer entertainNum;

    @Schema(description = "招待日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] entertainTime;

    @Schema(description = "招待对象名称", example = "芋艿")
    private String entertainName;

    @Schema(description = "招待对象类型", example = "2")
    private Integer entertainType;

    @Schema(description = "一般费用名称")
    private String generalExpense;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "是否申请报销")
    private Integer isApplyExpense;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "申请单类型", example = "2")
    private Integer applyType;

    private JsonAmount amount;

    private String amountDesc;
    @Schema(description = "实际产生费用的人")
    private UserDept actualUser;

    private Long companyId;

    private String companyName;
    @Schema(description = "出差类型")
    private Integer travelType;
}