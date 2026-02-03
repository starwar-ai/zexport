package cn.iocoder.yudao.module.infra.controller.admin.rate.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 动态汇率 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CurrencysRateRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10415")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "日期")
    @ExcelProperty("日期")
    private String dailyCurrDate;

    @Schema(description = "币种", example = "李四")
    @ExcelProperty("币种")
    private String dailyCurrName;

    @Schema(description = "汇率")
    @ExcelProperty("汇率")
    private BigDecimal dailyCurrRate;

    @Schema(description = "来源 1-自动 0-手动")
    @ExcelProperty("来源 1-自动 0-手动")
    private Integer dailyCurrSource;

    @Schema(description = "中间汇率")
    @ExcelProperty("中间汇率")
    private BigDecimal dailyCurrMidRate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}