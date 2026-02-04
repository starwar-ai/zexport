package com.syj.eplus.module.wms.dal.dataobject.transferorderitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 调拨明细 DO
 *
 * @author du
 */

@TableName("wms_transfer_order_item")
@KeySequence("wms_transfer_order_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferOrderItem extends BaseDO {

    @Schema(description = "主键")
    @TableId
    private Long id;

    @Schema(description = "调拨单主键")
    private Long transferOrderId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "库存明细主键")
    private Long stockId;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "产品名称")
    private Long skuName;

    @Schema(description = "库存批次号")
    private String batchCode;

    @Schema(description = "当前可用库存")
    private BigDecimal availableQuantity;

    @Schema(description = "拨出数量")
    private BigDecimal transferQuantity;

    @Schema(description = "销售订单号")
    private String saleContractCode;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "自主品牌标识")
    @TableField(exist = false)
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    @TableField(exist = false)
    private Integer custProFlag;

    @Schema(description = "入库时间")
    @TableField(exist = false)
    private LocalDateTime receiptTime;

    @Schema(description = "入库数量（批次入库时的库存）")
    @TableField(exist = false)
    private Integer initQuantity;

    @Schema(description = "出库数量（已出库库存汇总）")
    @TableField(exist = false)
    private Integer usedQuantity;

    @Schema(description = "锁定数量（锁定数量汇总）")
    @TableField(exist = false)
    private Integer lockQuantity;

    @Schema(description = "外箱装量")
    @TableField(exist = false)
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @TableField(exist = false)
    private Integer qtyPerInnerbox;

    @Schema(description = "单价")
    @TableField(exist = false)
    private JsonAmount price;

    @Schema(description = "总金额")
    @TableField(exist = false)
    private JsonAmount totalAmount;

    @Schema(description = "采购合同主键")
    @TableField(exist = false)
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @TableField(exist = false)
    private String purchaseContractCode;

    @Schema(description = "销售合同主键")
    @TableField(exist = false)
    private Long saleContractId;

    @Schema(description = "仓库主键")
    @TableField(exist = false)
    private Long warehouseId;

    @Schema(description = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @Schema(description = "存放位置")
    @TableField(exist = false)
    private String position;

    @Schema(description = "供应商主键")
    @TableField(exist = false)
    private Long venderId;

    @Schema(description = "供应商编码")
    @TableField(exist = false)
    private String venderCode;

    @Schema(description = "供应商名称")
    @TableField(exist = false)
    private String venderName;

    @Schema(description = "客户主键")
    @TableField(exist = false)
    private Long custId;

    @Schema(description = "客户PO号")
    @TableField(exist = false)
    private String custPo;

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    @TableField(exist = false)
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    @TableField(exist = false)
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    @TableField(exist = false)
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    @TableField(exist = false)
    private JsonWeight palletWeight;

    @Schema(description = "归属公司主键")
    @TableField(exist = false)
    private Long companyId;

    @Schema(description = "归属公司名称")
    @TableField(exist = false)
    private String companyName;

    @Schema(description = "备注")
    @TableField(exist = false)
    private String remark;

    /**
     * 规格
     */
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}