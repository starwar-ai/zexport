package com.syj.eplus.module.home.controller.admin.hometab.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 系统首页 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HomeTabRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11973")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户编号", example = "11997")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "角色编号", example = "22237")
    @ExcelProperty("角色编号")
    private Long roleId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}