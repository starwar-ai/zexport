package cn.iocoder.yudao.module.infra.controller.admin.rate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 动态汇率新增/修改 Request VO")
@Data
public class CurrencysRateSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10415")
    private Long id;

    @Schema(description = "日期")
    private String dailyCurrDate;

    @Schema(description = "币种", example = "李四")
    private String dailyCurrName;

    @Schema(description = "汇率")
    private BigDecimal dailyCurrRate;

    @Schema(description = "来源 1-自动 0-手动")
    private Integer dailyCurrSource;

    @Schema(description = "中间汇率")
    private BigDecimal dailyCurrMidRate;

}