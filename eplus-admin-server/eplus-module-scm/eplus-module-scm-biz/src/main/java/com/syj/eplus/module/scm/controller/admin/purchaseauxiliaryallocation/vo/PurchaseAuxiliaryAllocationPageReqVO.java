package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购合同辅料分摊分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseAuxiliaryAllocationPageReqVO extends PageParam {

    @Schema(description = "采购合同主键", example = "21266")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "采购合同明细主键", example = "20282")
    private Long purchaseContractItemId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称", example = "李四")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "辅料采购合同主键", example = "18880")
    private Long auxiliaryPurchaseContractId;

    @Schema(description = "辅料采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料采购合同明细主键", example = "9513")
    private Long auxiliaryPurchaseContractItemId;

    @Schema(description = "辅料产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料产品名称", example = "芋艿")
    private String auxiliarySkuName;

    @Schema(description = "采购数量")
    private String quantity;

    @Schema(description = "分摊金额")
    private JsonAmount allocationAmount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}