package com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo;


import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 加工单子产品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ManufactureSkuItemPageReqVO extends PageParam {

    @Schema(description = "加工单产品id", example = "17983")
    private Long manufactureSkuId;

    @Schema(description = "加工单id", example = "2510")
    private Long manufactureId;

    @Schema(description = "产品id", example = "2482")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户产品编号")
    private String cskuCode;

    @Schema(description = "产品名称", example = "芋艿")
    private String skuName;

    @Schema(description = "产品数量")
    private Integer quantity;

    @Schema(description = "配比")
    private Integer ratio;

    @Schema(description = "产品图片")
    private SimpleFile mainPicture;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}