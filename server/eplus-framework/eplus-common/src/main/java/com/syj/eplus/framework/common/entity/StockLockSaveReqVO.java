package com.syj.eplus.framework.common.entity;

import lombok.Data;

@Data
public class StockLockSaveReqVO {

    /**
     * 产品库存主键
     */
    private Long stockId;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 产品编码
     */
    private String skuCode;

//    @Schema(description = "原单据主键")
//    private Long sourceOrderId;

//    @Schema(description = "原单据明细主键", example = "1024")
//    private  Long sourceOrderItemId;
//
    /**
     * 原单据单号
     */
    private String sourceOrderCode;

    /**
     * 外销合同明细主键
     */
    private Long saleContractItemId;

    /**
     * 外销合同主键
     */
    private Long saleContractId;

    /**
     * 外销合同单号
     */
    private String saleContractCode;

    /**
     * 原单据类型  1-销售合同 2-加工单 3-调拨单 4-采购计划
     */
    private Integer sourceOrderType;

    /**
     * 单据-锁定数量
     */
    private Integer sourceOrderLockedQuantity;

    /**
     * 锁定数量
     */
    private Integer lockQuantity;

    /**
     * 归属公司主键
     */
    private Long companyId;
    /**
     * 归属公司名称
     */
    private String companyName;

    /**
     * 备注
     */
    private String remark;

}
