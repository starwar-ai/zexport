package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanItemToContractItemRespVO extends  SimpleSkuDTO {

    @Schema(description = "采购类型")
    private Integer purchaseType;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "是否赠品")
    private Integer freeFlag;


    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员名称")
    private String purchaseUserName;
    @Schema(description = "采购单来源")
    private Integer sourceType;

    @CompareField(value = "销售数量")
    private Integer saleQuantity;

    @Schema(description = "可用库存")
    private Integer availableQuantity;

    @CompareField(value = "锁定数量")
    private Integer currentLockQuantity;

    @CompareField(value = "待采购数量")
    private Integer needPurQuantity;

    @CompareField(value = "产品删除标记")
    private Integer skuDeletedFlag;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "已转合同数量")
    private Integer convertedQuantity;

    @Schema(description = "客户po号")
    private String custPo;
}
