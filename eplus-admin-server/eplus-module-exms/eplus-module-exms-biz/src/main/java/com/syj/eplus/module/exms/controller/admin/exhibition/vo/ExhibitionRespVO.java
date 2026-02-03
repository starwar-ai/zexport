package com.syj.eplus.module.exms.controller.admin.exhibition.vo;

import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 展会 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExhibitionRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31339")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "名称", example = "芋艿")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "项目状态", example = "1")
    @ExcelProperty("项目状态")
    private Integer expoStatus;
    
    @Schema(description = "审核状态", example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;
    
    @Schema(description = "主体id", example = "10113")
    @ExcelProperty("主体id")
    private Long companyId;
    
    @Schema(description = "主体名称", example = "李四")
    @ExcelProperty("主体名称")
    private String companyName;
    
    @Schema(description = "承担费用部门id列表")
    @ExcelProperty("承担费用部门id列表")
    private List<Long> deptList;

    @Schema(description = "承担费用部门列表")
    @ExcelProperty("承担费用部门列表")
    private List<DeptRespDTO> deptMsgList;
    
    @Schema(description = "申请日期")
    @ExcelProperty("申请日期")
    private LocalDateTime applyDate;
    
    @Schema(description = "申请人id", example = "21262")
    @ExcelProperty("申请人id")
    private Long applyUserId;
    
    @Schema(description = "申请人姓名", example = "李四")
    @ExcelProperty("申请人姓名")
    private String applyUserName;
    
    @Schema(description = "申请人部门id", example = "27294")
    @ExcelProperty("申请人部门id")
    private Long applyDeptId;
    
    @Schema(description = "申请人部门名称", example = "张三")
    @ExcelProperty("申请人部门名称")
    private String applyDeptName;
    
    @Schema(description = "项目预算")
    @ExcelProperty("项目预算")
    private JsonAmount budget;
    
    @Schema(description = "展会主题")
    @ExcelProperty("展会主题")
    private Integer theme;
    
    @Schema(description = "摊位主题")
    @ExcelProperty("摊位主题")
    private List<Integer> stallThemeList;
    
    @Schema(description = "国家id", example = "16053")
    @ExcelProperty("国家id")
    private Long countryId;
    
    @Schema(description = "国家名称", example = "李四")
    @ExcelProperty("国家名称")
    private String countryName;
    
    @Schema(description = "城市id", example = "21163")
    @ExcelProperty("城市id")
    private Long cityId;
    
    @Schema(description = "城市名称", example = "芋艿")
    @ExcelProperty("城市名称")
    private String cityName;
    
    @Schema(description = "计划开始日期")
    @ExcelProperty("计划开始日期")
    private LocalDateTime planStartDate;
    
    @Schema(description = "计划结束日期")
    @ExcelProperty("计划结束日期")
    private LocalDateTime planEndDate;
    
    @Schema(description = "实际开始日期")
    @ExcelProperty("实际开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "实际结束日期")
    @ExcelProperty("实际结束日期")
    private LocalDateTime endDate;
    
    @Schema(description = "摊位面积（平米）")
    @ExcelProperty("摊位面积（平米）")
    private String stallArea;
    
    @Schema(description = "负责人id", example = "4781")
    @ExcelProperty("负责人id")
    private Long ownerUserId;
    
    @Schema(description = "负责人姓名", example = "张三")
    @ExcelProperty("负责人姓名")
    private String ownerUserName;
    
    @Schema(description = "负责人部门id", example = "23629")
    @ExcelProperty("负责人部门id")
    private Long ownerDeptId;
    
    @Schema(description = "负责人部门名称", example = "赵六")
    @ExcelProperty("负责人部门名称")
    private String ownerDeptName;
    
    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;
    
    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime doneTime;
    
    @Schema(description = "结案时间")
    @ExcelProperty("结案时间")
    private LocalDateTime finishTime;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    /////前端转换字段,重写planStartDate和planEndDAte
    @Schema(description = "计划日期")
    @ExcelIgnore
    private LocalDateTime[] planDate;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "展会系列id")
    private Long exmsEventCategoryId;

    @Schema(description = "展会系列名称")
    private String exmsEventCategoryName;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer orderNum;
}