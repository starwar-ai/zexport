package com.syj.eplus.module.exms.controller.admin.exhibition.vo;


import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 展会分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExhibitionPageReqVO extends PageParam {

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
    private List<String>  deptList;

    @Schema(description = "申请日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyDate;

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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] planStartDate;

    @Schema(description = "计划结束日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] planEndDate;

    @Schema(description = "实际开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startDate;

    @Schema(description = "实际结束日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endDate;

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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] doneTime;

    @Schema(description = "结案时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] finishTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @CompareField(value = "展会系列id")
    private Long exmsEventCategoryId;

}