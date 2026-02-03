package com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 产品辅料配比分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkuAuxiliaryPageReqVO extends PageParam {

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称", example = "王五")
    private String skuName;

    @Schema(description = "辅料编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料名称", example = "李四")
    private String auxiliarySkuName;

    @Schema(description = "产品比")
    private Integer skuRate;

    @Schema(description = "辅料比")
    private Integer auxiliarySkuRate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}