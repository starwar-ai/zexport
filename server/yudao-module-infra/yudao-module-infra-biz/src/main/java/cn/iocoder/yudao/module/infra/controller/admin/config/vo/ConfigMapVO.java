package cn.iocoder.yudao.module.infra.controller.admin.config.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 参数配置信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConfigMapVO {
    @Schema(description = "参数键名" )
    private String key;

    @Schema(description = "参数键值" )
    private String value;

}
