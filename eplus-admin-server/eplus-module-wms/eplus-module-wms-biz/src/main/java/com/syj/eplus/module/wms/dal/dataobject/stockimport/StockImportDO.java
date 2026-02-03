package com.syj.eplus.module.wms.dal.dataobject.stockimport;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓储管理-库存明细导入 DO
 *
 * @author zhangcm
 */

@TableName(value = "wms_stock_import",autoResultMap = true)
@KeySequence("wms_stock_import_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockImportDO extends BaseDO {
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
     * 单据-主键
     */
    private Long billId;
    /**
     * 单据明细-主键
     */
    private Long billItemId;
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
     * 基础产品编号
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
     * 入库时间
     */
    private LocalDateTime receiptTime;
    /**
     * 在制数量(采购合同下单时库存)
     */
    private Integer producingQuantity;
    /**
     * 入库数量（批次入库时的库存）
     */
    private Integer initQuantity;
    /**
     * 出库数量（已出库库存汇总）
     */
    private Integer usedQuantity;
    /**
     * 锁定数量（锁定数量汇总）
     */
    private Integer lockQuantity;
    /**
     * 可用数量（可用数量=在制数量+入库数量-出库数量-锁定数量 无销售合同）
     */
    private Integer availableQuantity;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount price;
    /**
     * 总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;
    /**
     * 采购合同主键
     */
    private Long purchaseContractId;
    /**
     * 采购合同号
     */
    private String purchaseContractCode;
    /**
     * 外销合同主键
     */
    private Long saleContractId;
    /**
     * 外销合同号
     */
    private String saleContractCode;
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
     * 总体积(单箱体积x箱数cm³)
     */
    private BigDecimal totalVolume;
    /**
     * 总毛重（单箱毛重x箱数 {数量,单位}）
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;
    /**
     * 单托体积（cm³）
     */
    private BigDecimal palletVolume;
    /**
     * 单托毛重（{数量,单位}）
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight palletWeight;
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
     * 剩余总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount remainTotalAmount;

    /**
     * 盘点数量
     */
    private Integer diffQuantity;

    /**
     * 采购员
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept purchaseUser;

    /**
     * 拉柜数量
     */
    private Integer cabinetQuantity;

    /**
     * 规格
     */
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
    /**
     * 失败原因
     */
    private String errorRemark;
    /**
     * 失败原因
     */
    private Integer errorFlag;
    /**
     * 导入批次号
     */
    private String importCode;

    private BigDecimal outerboxLength;


    private BigDecimal outerboxWidth;


    private BigDecimal outerboxHeight;

    private Integer stockFlag;


}