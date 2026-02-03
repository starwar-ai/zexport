package com.syj.eplus.module.exms.controller.admin.exhibition.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 展会新增/修改 Request VO")
@Data
public class ExhibitionSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31339")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "项目状态", example = "1")
    private Integer expoStatus;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "主体id", example = "10113")
    private Long companyId;

    @Schema(description = "主体名称", example = "李四")
    private String companyName;

    @Schema(description = "承担费用部门列表")
    private List<Long>  deptList;

    @Schema(description = "申请日期")
    private LocalDateTime applyDate;

    @Schema(description = "申请人id", example = "21262")
    private Long applyUserId;

    @Schema(description = "申请人姓名", example = "李四")
    private String applyUserName;

    @Schema(description = "申请人部门id", example = "27294")
    private Long applyDeptId;

    @Schema(description = "申请人部门名称", example = "张三")
    private String applyDeptName;

    @Schema(description = "项目预算")
    private JsonAmount budget;

    @Schema(description = "展会主题")
    private Integer theme;

    @Schema(description = "摊位主题")
    private List<Integer> stallThemeList;

    @Schema(description = "国家id", example = "16053")
    private Long countryId;

    @Schema(description = "国家名称", example = "李四")
    private String countryName;

    @Schema(description = "城市id", example = "21163")
    private Long cityId;

    @Schema(description = "城市名称", example = "芋艿")
    private String cityName;

    @Schema(description = "计划开始日期")
    private LocalDateTime planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDateTime planEndDate;

    @Schema(description = "实际开始日期")
    private LocalDateTime startDate;

    @Schema(description = "实际结束日期")
    private LocalDateTime endDate;

    @Schema(description = "摊位面积（平米）")
    private String stallArea;

    @Schema(description = "负责人id", example = "4781")
    private Long ownerUserId;

    @Schema(description = "负责人姓名", example = "张三")
    private String ownerUserName;

    @Schema(description = "负责人部门id", example = "23629")
    private Long ownerDeptId;

    @Schema(description = "负责人部门名称", example = "赵六")
    private String ownerDeptName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "完成时间")
    private LocalDateTime doneTime;

    @Schema(description = "结案时间")
    private LocalDateTime finishTime;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;


    /////前端转换字段,重写planStartDate和planEndDAte
    @Schema(description = "计划日期")
    private LocalDateTime[] planDate;

    @Schema(description = "展会系列id")
    private Long exmsEventCategoryId;

    @Schema(description = "展会系列名称")
    private String exmsEventCategoryName;

}