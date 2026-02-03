package com.syj.eplus.module.wms.dal.dataobject.adjustment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.*;

/**
 * 仓储管理-盘库调整单-明细 DO
 *
 * @author ePlus
 */

@TableName(value = "wms_adjustment_item",autoResultMap = true)
@KeySequence("wms_adjustment_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 调整单主键
     */
    private Long adjustmentId;
    /**
     * 产品主键
     */
    private Long skuId;
    /**
     * 产品编码
     */
    private String skuCode;
    /**
     * 产品中文名称
     */
    private String skuName;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 客户货号
     */
    private String basicSkuCode;
    /**
     * 自主品牌标识
     */
    private Integer ownBrandFlag;
    /**
     * 客户产品标识
     */
    private Integer custProFlag;
    /**
     * 产品序号
     */
    private Integer sortNum;
    /**
     * 来源单据明细序号
     */
    private Integer sourceSortNum;
    /**
     * 仓库主键
     */
    private Long warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 存放位置
     */
    private String position;
    /**
     * 盘点位置
     */
    private String stocktakePosition;
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    /**
     * 盘点数量
     */
    private Integer stocktakeStockQuantity;
    /**
     * 盘盈/盘亏数量
     */
    private Integer differenceQuantity;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 库存箱数
     */
    private Integer stockBoxQuantity;
    /**
     * 盘点箱数
     */
    private Integer stocktakeStockBoxQuantity;
    /**
     * 盘盈/盘亏箱数
     */
    private Integer differenceBoxQuantity;
    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 供应商编码
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 客户主键
     */
    private Long custId;
    /**
     * 客户编码
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 客户PO号
     */
    private String custPo;
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
     * 单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount price;

    /**
     * 总价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;

    /**
     * 采购合同号
     */
    private String purchaseContractCode;

    /**
     * 销售合同号
     */
    private String saleContractCode;

    /**
     * 差异原因
     */
    private String diffReasons;
}
