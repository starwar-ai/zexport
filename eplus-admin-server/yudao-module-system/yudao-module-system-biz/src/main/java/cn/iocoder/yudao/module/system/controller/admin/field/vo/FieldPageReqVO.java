package cn.iocoder.yudao.module.system.controller.admin.field.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 系统字段分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FieldPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}