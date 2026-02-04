package com.syj.eplus.module.oa.controller.admin.apply.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - OA申请单新增/修改 Request VO")
@Data
public class ApplySaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25949")
    private Long id;

    @Schema(description = "拟达成目标", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intendedObjectives;

    @Schema(description = "单据编号")
    private String code;

    @Schema(description = "企微申请单id", example = "21890")
    private String wecomId;

    @Schema(description = "申请人", example = "31322")
    private UserDept applyer;
    @Schema(description = "产生人", example = "31322")
    private UserDept actualUser;
    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "出差事由")
    private String purpose;

    @Schema(description = "出差地点")
    private String dest;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "出差时长(秒)")
    private Integer duration;

    @Schema(description = "交通工具", example = "1")
    private Integer transportationType;

    @Schema(description = "同行人员")
    private List<UserDept> entertainEntourage;

    @Schema(description = "招待对象等级")
    private Integer entertainLevel;

    @Schema(description = "招待人数")
    private Integer entertainNum;

    @Schema(description = "招待日期")
    private LocalDateTime entertainTime;

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

    @Schema(description = "申请单类型", example = "2")
    private Integer applyType;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "附件", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SimpleFile> annex;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    private JsonAmount amount;

    private String amountDesc;

    @Schema(description = "费用归集数据")
    private List<FeeShareReqDTO> feeShare;

    private Long companyId;

    private String companyName;

    @Schema(description = "出差类型")
    private Integer travelType;

    @Schema(description = "申请报销次数")
    private Integer applyExpenseTimes;

}