package com.syj.eplus.module.wms.dal.dataobject.bill;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
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
import java.util.List;

/**
 * 仓储管理-入(出)库单-明细 DO
 *
 * @author ePlus
 */
@TableName(value = "wms_bill_item", autoResultMap = true)
@KeySequence("wms_bill_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItemDO extends BaseDO {

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
     * 来源单主键
     */
    private Long sourceId;
    /**
     * 来源单单号
     */
    private String sourceCode;
    /**
     * 来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库
     *
     * 枚举 {@link TODO stock_source_type 对应的类}
     */
    private Integer sourceType;
    /**
     * 入/出库类型 1-入库、2-出库
     */
    private Integer billType;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 来源单据明细序号
     */
    private Integer sourceSortNum;
    /**
     * 入/出库状态（未收/出货、部分收/出货、完全收/出货）
     *
     * 枚举 {@link TODO in_out_status 对应的类}
     */
    private Integer noticeItemStatus;
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
     * 应收/出数量
     */
    private Integer orderQuantity;
    /**
     * 应收/出箱数
     */
    private Integer orderBoxQuantity;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 实收/出数量
     */
    private Integer actQuantity;
    /**
     * 实收/出箱数
     */
    private Integer actBoxQuantity;
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
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 异常状态
     */
    private Integer abnormalStatus;

    /**
     * 异常说明
     */
    private String abnormalRemark;

    /**
     * 销售合同明细编号
     */
    private String saleItemUniqueCode;
    /**
     * 采购合同主键
     */
    private Long purchaseContractId;
    /**
     * 采购合同号
     */
    private String purchaseContractCode;
    /**
     * 采购员主键
     */
    private Long purchaserId;
    /**
     * 采购员部门主键
     */
    private Long purchaserDeptId;

    /**
     * 跟单员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    /**
     * 价格
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount price;

    /**
     * 采购合同明细编号
     */
    private String purchaseItemUniqueCode;

    /**
     * 出运明细id
     */
    private Long shipmentItemId;

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
     * 自营货号
     */
    private String oskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    private String barcode;
}
