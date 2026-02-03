package cn.iocoder.yudao.module.system.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 系统字段新增/修改 Request VO")
@Data
public class FieldSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1246")
    private Long id;

    @Schema(description = "表名称", example = "张三")
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = "字段名称", example = "张三")
    private String fieldName;

    @Schema(description = "字段注释")
    private String fieldComment;

    @Schema(description = "字段类型", example = "1")
    private String fieldType;

    @Schema(description = "是否字典")
    private Integer dictFlag;

    @Schema(description = "字典类型", example = "1")
    private String dictType;

}