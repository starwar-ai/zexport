package com.syj.eplus.module.pms.controller.admin.brand.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 14:45
 */
@Data
public class BrandSimpleRespVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "名称")
    private String name;

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
