package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 费用归集 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FeeShareRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16564")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "来源类型", example = "2")
    @ExcelProperty("来源类型")
    private Integer businessType;
    
    @Schema(description = "来源单编号")
    @ExcelProperty("来源单编号")
    private String businessCode;
    
    @Schema(description = "业务部门id", example = "26115")
    @ExcelProperty("业务部门id")
    private Long deptId;
    
    @Schema(description = "业务部门名称", example = "李四")
    @ExcelProperty("业务部门名称")
    private String deptName;
    
    @Schema(description = "费用归属类别", example = "1")
    @ExcelProperty("费用归属类别")
    private Integer feeShareType;
    
    @Schema(description = "相关方类别", example = "2")
    @ExcelProperty("相关方类别")
    private Integer relationType;
    
    @Schema(description = "具体名称id", example = "26364")
    @ExcelProperty("具体名称id")
    private Long descId;
    
    @Schema(description = "具体名称", example = "芋艿")
    @ExcelProperty("具体名称")
    private String descName;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "流程实例编号", example = "芋艿")
    private String processInstanceId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "支付状态", example = "芋艿")
    private Integer paymentStatus;

    @Schema(description = "主体主键", example = "芋艿")
    private Long companyId;

    @Schema(description = "主体名称", example = "芋艿")
    private String companyName;

    @Schema(description = "来源单据状态", example = "芋艿")
    private Integer sourceStatus;

    @Schema(description = "来源单据主键")
    private Integer businessId;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "金额")
    private JsonAmount amount;

    @Schema(description = "订单类型")
    private Integer orderType;

    private UserDept shareUser;

    private String feeShareDetail;
    @Schema(description = "预归集标记")
    private Integer preCollectionFlag;

    @Schema(description = "项目id")
    private Long projectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "展会id")
    private Long exhibitionId;

    @Schema(description = "展会名称")
    private String exhibitionName;

    @Schema(description = "品牌名称")
    private Integer brandType;


}