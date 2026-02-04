package com.syj.eplus.module.pms.controller.admin.skubom.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 产品SKU BOM分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkuBomPageReqVO extends PageParam {

    @Schema(description = "子SKU编号", example = "22451")
    private Long skuId;

    @Schema(description = "SKU版本")
    private Integer skuVer;

    @Schema(description = "数量")
    private Integer qty;

    @Schema(description = "父SKU编号", example = "27839")
    private Long parentSkuId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}