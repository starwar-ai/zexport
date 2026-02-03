package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 出运单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShipmentPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "外销合同")
    private String saleContractCode;

    @Schema(description = "客户合同")
    private String custContractCode;

    @Schema(description = "内部法人单位主键", example = "21247")
    private Long foreignTradeCompanyId;

    @Schema(description = "佣金金额")
    private String commissionAmount;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态列表")
    private List<Integer> statusList;

    @Schema(description = "id数组")
    private List<Long> idList;


    @Schema(description = "录入日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inputDate;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shipmentPlanCode;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "状态")
    private Integer neStatus;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    private Boolean isTree ;

    private String invoiceCode;
    private String custName;
    private Long inputUserId;

    @Schema(description = "分批出运标识")
    private Integer batchFlag;

    @Schema(description = "排除的id")
    private Long exceptId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "客户合同号")
    private String custPo;
}