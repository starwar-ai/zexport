package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 出运计划单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShipmentPlanPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "单据状态", example = "2")
    private Integer status;

    @Schema(description = "外销合同号")
    private String saleContractCode;

    @Schema(description = "内部法人单位主键", example = "4829")
    private Long foreignTradeCompanyId;

    @Schema(description = "状态")
    private Integer neStatus;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "客户PO")
    private String custPo;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "id数组")
    private List<Long> idList;

    @Schema(description = "客户名称")
    private String custName;
}