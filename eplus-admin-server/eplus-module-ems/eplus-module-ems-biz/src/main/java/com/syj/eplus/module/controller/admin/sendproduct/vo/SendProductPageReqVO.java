package com.syj.eplus.module.controller.admin.sendproduct.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 寄件产品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SendProductPageReqVO extends PageParam {

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "物件来源")
    private Integer goodsSource;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称", example = "张三")
    private String skuName;

    @Schema(description = "图片")
    private String picture;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}