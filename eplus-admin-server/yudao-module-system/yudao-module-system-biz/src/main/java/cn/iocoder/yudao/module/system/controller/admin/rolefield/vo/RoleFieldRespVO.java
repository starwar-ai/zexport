package cn.iocoder.yudao.module.system.controller.admin.rolefield.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 字段角色关联 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RoleFieldRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27194")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "字段id", example = "15354")
    @ExcelProperty("字段id")
    private Long fieldId;

    @Schema(description = "角色id", example = "5202")
    @ExcelProperty("角色id")
    private Long roleId;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}