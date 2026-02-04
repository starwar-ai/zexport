package com.syj.eplus.module.pms.controller.admin.brand.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 品牌新增/修改 Request VO")
@Data
public class BrandSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "条码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "流水号长度")
    private String nameEng;

    @Schema(description = "商品说明")
    private String description;

    @Schema(description = "商品说明英文")
    private String descriptionEng;

    @Schema(description = "是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "品牌类型")
    private Integer brandType;

    @Schema(description = "客户id")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "品牌logo")
    private SimpleFile logo;


}