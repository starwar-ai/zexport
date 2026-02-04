package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 报价单子分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuotationItemPageReqVO extends PageParam {

    @Schema(description = "产品编号", example = "31865")
    private String skuCode;

    @Schema(description = "客户主键", example = "小米手机")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户产品标识", example = "小米手机")
    private Integer custProFlag;

    @Schema(description = "自营产品标识", example = "小米手机")
    private Integer ownBrandFlag;

    @Schema(description = "报价单主键", example = "小米手机")
    private List<Long> smsQuotationIdList;

}
