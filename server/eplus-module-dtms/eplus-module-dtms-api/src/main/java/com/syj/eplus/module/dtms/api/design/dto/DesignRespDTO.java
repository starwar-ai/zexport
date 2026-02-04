package com.syj.eplus.module.dtms.api.design.dto;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 设计-任务单 Response VO")
@Data
public class DesignRespDTO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16038")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "设计任务名称", example = "芋艿")
    private String name;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "任务状态：1：待提交，2：待审批，3：待完成，4：待评价，5：已完成，6：已作废，7：已驳回", example = "1")
    private Integer designStatus;

    @Schema(description = "是否特批:0-否 1-是")
    private Integer specialPermissionFlag;

    @Schema(description = "特批原因", example = "不对")
    private String specialPermissionReason;

    @Schema(description = "设计任务类型（多选）1：亚马逊，2：阿里，3：拍照抠图P图，4：包材设计，5：不干胶设计及打印，6：视频拍摄制作，7：效果图设计，8：样本宣传页", example = "1")
    private String designType;

    @Schema(description = "合同类型")
    private Integer contractType;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "期望完成时间")
    private LocalDateTime expectCompleteDate;

    @Schema(description = "实际完成时间")
    private LocalDateTime completeDate;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "申请人主键", example = "18329")
    private Long applyDesignerId;

    @Schema(description = "申请人姓名", example = "赵六")
    private String applyDesignerName;

    @Schema(description = "申请人部门主键", example = "7406")
    private Long applyDesignerDeptId;

    @Schema(description = "申请人部门名称", example = "李四")
    private String applyDesignerDeptName;

    @Schema(description = "设计要求")
    private String designRequirement;

    @Schema(description = "素材说明")
    private String materialDesc;

    @Schema(description = "认领人主键")
    private String designerIds;

    @Schema(description = "认领人姓名")
    private String designerNames;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "指定设计师")
    private List<UserDept> specificDesigners;

    @Schema(description = "是否补单")
    private Long isSupplementOrder;

}
