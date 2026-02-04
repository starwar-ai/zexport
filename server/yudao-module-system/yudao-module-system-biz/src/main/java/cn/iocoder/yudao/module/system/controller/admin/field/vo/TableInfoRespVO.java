package cn.iocoder.yudao.module.system.controller.admin.field.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/2 15:57
 */
@Schema(description = "管理后台 - 系统表 Response VO")
@Data
public class TableInfoRespVO {
    @Schema(description = "表名称", example = "张三")
    @ExcelProperty("表名称")
    private String tableName;

    @Schema(description = "表描述")
    @ExcelProperty("表描述")
    private String tableComment;

}
