package cn.iocoder.yudao.module.system.controller.admin.priceType.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 价格条款 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PriceTypeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3628")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "名称", example = "王五")
    @ExcelProperty("名称")
    private String typeName;

    @Schema(description = "描述")
    @ExcelProperty("描述")
    private String typeDesc;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}