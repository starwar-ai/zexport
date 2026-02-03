package cn.iocoder.yudao.module.system.controller.admin.priceType.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 价格条款新增/修改 Request VO")
@Data
public class PriceTypeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3628")
    private Long id;

    @Schema(description = "名称", example = "王五")
    private String typeName;

    @Schema(description = "描述")
    private String typeDesc;

}