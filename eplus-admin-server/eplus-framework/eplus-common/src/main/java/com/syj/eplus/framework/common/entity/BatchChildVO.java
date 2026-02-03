package com.syj.eplus.framework.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Description:
 */
@Data
public class BatchChildVO {
    /** 批次号 */
    private String batchCode;

    /** 可用数量（仓库中可占用可出库的库存，可用数量= 入库数量-出库数量-锁定数量） */
    private Integer availableQuantity;

    /** 在制数量（采购合同下单时库存） */
    private Integer producingQuantity;

    /** 可拉柜数量 */
    private Integer availableCabinetQuantity;

    /** 仓库主键 */
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;

    /** 公司名称 */
    private String companyName;

    /** 入库日期 */
    private LocalDateTime inboundDate;

    /** 采购合同id */
    private Long purchaseContractId;

    /** 采购合同编号 */
    private String purchaseContractCode;

    /** 供应商主键 */
    private Long venderId;

    /** 供应商编码 */
    private String venderCode;

    /** 供应商名称 */
    private String venderName;

    /** 箱数 */
    private Integer boxCount;

    /** 外箱装量 */
    private Integer qtyPerOuterbox;

    /** 内盒装量 */
    private Integer qtyPerInnerbox;

    /** 总体积 */
    private BigDecimal totalVolume;

    /** 总净重 */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalNetweight;

    /** 总毛重 */
    private JsonWeight totalGrossweight;

    /** 使用库存数量 */
    private Integer usedQuantity;

    /** 库存方式 */
    private Integer stockMethod;

    /** 出运明细id */
    private Long shipmentItemId;

    /** 规格 **/
    private List<JsonSpecificationEntity> specificationList;
    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;


    /**
     * 发货地址
     */
    private Integer shippedAddress;
}