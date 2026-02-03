package cn.iocoder.yudao.module.system.controller.admin.field.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 系统字段 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FieldRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1246")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "表名称", example = "张三")
    @ExcelProperty("表名称")
    private String tableName;

    @Schema(description = "表描述")
    @ExcelProperty("表描述")
    private String tableComment;

    @Schema(description = "字段名称", example = "张三")
    @ExcelProperty("字段名称")
    private String fieldName;

    @Schema(description = "字段注释")
    @ExcelProperty("字段注释")
    private String fieldComment;

    @Schema(description = "字段类型", example = "1")
    @ExcelProperty("字段类型")
    private String fieldType;

    @Schema(description = "是否字典")
    @ExcelProperty(value = "是否字典", converter = DictConvert.class)
    @DictFormat("confirm_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer dictFlag;

    @Schema(description = "字典类型", example = "1")
    @ExcelProperty("字典类型")
    private String dictType;

    @Schema(description = "是否有权限", example = "1")
    @ExcelProperty("是否有权限")
    private Integer fieldPermissionFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}