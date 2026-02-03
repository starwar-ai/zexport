package com.syj.eplus.module.mms.controller.admin.manufacturesku.vo;


import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 加工单产品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ManufactureSkuPageReqVO extends PageParam {

    @Schema(description = "加工单id", example = "9979")
    private Long manufactureId;

    @Schema(description = "产品id", example = "6010")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户产品编号")
    private String cskuCode;

    @Schema(description = "产品名称", example = "芋艿")
    private String skuName;

    @Schema(description = "产品数量")
    private Integer quantity;

    @Schema(description = "产品图片")
    private SimpleFile mainPicture;

    @Schema(description = "销售合同id", example = "7925")
    private Long smsContractId;

    @Schema(description = "销售合同编号")
    private String smsContractCode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}