package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonStockDTO {

    /** 主键 */
    private Long id;

    /** 批次号 */
    private String batchCode;

    /** 入库单明细-主键 */
    private Long billItemId;

    /** 产品主键 */
    private Long skuId;

    /** 产品编码 */
    private String skuCode;

    /** 产品中文名称 */
    private String skuName;

    /** 客户货号 */
    private String cskuCode;

    /** 自主品牌标识 */
    private Integer ownBrandFlag;

    /** 客户产品标识 */
    private Integer custProFlag;

    /** 入库时间 */
    private LocalDateTime receiptTime;

    /** 入库数量（批次入库时的库存） */
    private Integer initQuantity;

    /** 出库数量（已出库库存汇总） */
    private Integer usedQuantity;

    /** 锁定数量（锁定数量汇总） */
    private Integer lockQuantity;

    /** 可用数量（仓库中可占用可出库的库存，可用数量= 入库数量-出库数量-锁定数量） */
    private Integer availableQuantity;

    /** 在制数量（采购合同下单时库存） */
    private Integer producingQuantity;

    /** 外箱装量 */
    private Integer qtyPerOuterbox;

    /** 内盒装量 */
    private Integer qtyPerInnerbox;

    /** 单价 */
    private JsonAmount price;

    /** 总金额 */
    private JsonAmount totalAmount;

    /** 采购合同主键 */
    private Long purchaseContractId;

    /** 采购合同号 */
    private String purchaseContractCode;

    /** 销售合同主键 */
    private Long saleContractId;

    /** 销售合同号 */
    private String saleContractCode;

    /** 仓库主键 */
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;

    /** 存放位置 */
    private String position;

    /** 供应商主键 */
    private Long venderId;

    /** 供应商编码 */
    private String venderCode;

    /** 供应商名称 */
    private String venderName;

    /** 客户主键 */
    private Long custId;

    /** 客户编码 */
    private String custCode;

    /** 客户名称 */
    private String custName;

    /** 客户PO号 */
    private String custPo;

    /** 总体积(单箱体积x箱数cm³) */
    private BigDecimal totalVolume;

    /** 总毛重（单箱毛重x箱数 {数量,单位}） */
    private JsonWeight totalWeight;

    /** 单托体积（cm³） */
    private BigDecimal palletVolume;

    /** 单托毛重（{数量,单位}） */
    private JsonWeight palletWeight;

    /** 归属公司主键 */
    private Long companyId;

    /** 归属公司名称 */
    private String companyName;

    /** 备注 */
    private String remark;
    /**
     * 仓库状态
     */
    private Integer venderFlag;
    /**
     * 供应商仓库类别
     */
    private Integer venderWmsType;

    /**
     * 库存类型
     */
    private Integer stockMethod;

   /**规格**/
   private List<JsonSpecificationEntity> specificationList;
    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}
