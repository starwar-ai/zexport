package com.syj.eplus.module.wms.api.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Desc——库存-锁定响应信息
 * Create by Rangers at  2024-09-07 17:31
 */
@Schema(description = "管理后台 - 库存-锁定响应信息 Response VO")
@Data
public class StockLockRespVO {
    private Long id;
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
    /**
     * 原单据主键
     */
    private Long sourceOrderId;
//    /**
//     * 原单据-明细主键
//     */
//    private Long sourceOrderItemId;
//    /**
//     * 原单据单号
//     */
//    private String sourceOrderCode;
    /**
     * 外销合同主键
     */
    private Long saleContractId;
    /**
     * 外销合同主键
     */
    private Long saleContractItemId;
    /**
     * 外销合同单号
     */
    private String saleContractCode;
    /**
     * 原单据类型  1-销售合同 2-加工单 3-调拨单 4-采购计划
     */
    private Integer sourceOrderType;
    /**
     * 锁定类型  1-可用数量 2-未到货库存
     */
    private Integer lockType;
    /**
     * 锁定数量
     */
    private Integer lockQuantity;
    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;
    /**
     * 锁定人主键
     */
    private Long lockByUserId;
    /**
     * 锁定人名称
     */
    private String lockByUserName;
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

    /**
     * 拉柜数量
     */
    private Integer cabinetQuantity;
}
