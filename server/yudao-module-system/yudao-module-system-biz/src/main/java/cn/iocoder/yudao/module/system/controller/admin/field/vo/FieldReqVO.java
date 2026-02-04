package cn.iocoder.yudao.module.system.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/2 16:07
 */
@Schema(description = "管理后台 - 系统字段分页 Request VO")
@Data
@ToString(callSuper = true)
public class FieldReqVO {
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

    @Schema(description = "角色id", example = "1")
    private Long roleId;
}
