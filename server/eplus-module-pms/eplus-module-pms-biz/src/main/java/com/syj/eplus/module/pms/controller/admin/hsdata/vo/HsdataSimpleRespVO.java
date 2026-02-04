package com.syj.eplus.module.pms.controller.admin.hsdata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 11:44
 */
@Data
public class HsdataSimpleRespVO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "报关单位")
    private String unit;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;
}
