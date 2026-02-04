package cn.iocoder.yudao.module.system.controller.admin.port.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 口岸新增/修改 Request VO")
@Data
public class PortSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24999")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "国家id", example = "23727")
    private Long countryId;

}