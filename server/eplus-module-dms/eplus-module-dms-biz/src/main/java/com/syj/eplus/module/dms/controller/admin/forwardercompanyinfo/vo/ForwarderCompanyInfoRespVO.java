package com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 船代公司 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ForwarderCompanyInfoRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4395")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("联系人")
    private String contactName;
    
    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("联系电话")
    private String contactPhoneNumber;
    
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;
    
    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入人")
    private UserDept inputUser;
    
    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称")
    private String companyName;

    @Schema(description = "备注")
    private String remark;
}