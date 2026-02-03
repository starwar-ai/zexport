package com.syj.eplus.module.qms.dal.dataobject.qualityinspection;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.util.List;

/**
 * 验货单-明细 DO
 *
 * @author ePlus
 */
@TableName(value = "qms_quality_inspection_item",autoResultMap = true)
@KeySequence("qms_quality_inspection_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 验货单主键
     */
    private Long inspectionId;
    /**
     * 产品主键
     */
    private Long skuId;
    /**
     * 产品编号
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
     * 验货状态:1:待定 2：成功，3：失败，4：待验货 5：放行通过
     */    private Integer inspectionStatus;
    /**
     * 验货图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;
    /**
     * 失败描述
     */
    private String failDesc;
    /**
     * 上次失败描述
     */
    private String lastFailDesc;
    /**
     * 待定描述
     */
    private String pendingDesc;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 验货费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 产品总价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;
    /**
     * 采购合同号
     */
    private String purchaseContractCode;
    /**
     * 采购合同号
     */
    private Long purchaseContractItemId;
    /**
     * 采购员主键
     */
    private Long purchaseUserId;
    /**
     * 采购员姓名
     */
    private String purchaseUserName;
    /**
     * 采购员部门主键
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    private String purchaseUserDeptName;
    /**
     * 销售合同号
     */
    private String saleContractCode;
    /**
     * 销售员主键
     */
    private Long saleUserId;
    /**
     * 销售员姓名
     */
    private String saleUserName;
    /**
     * 销售员部门主键
     */
    private Long saleUserDeptId;
    /**
     * 销售员部门名称
     */
    private String saleUserDeptName;
    /**
     * 跟单员主键
     */
    private Long trackUserId;
    /**
     * 跟单员姓名
     */
    private String trackUserName;
    /**
     * 跟单员部门主键
     */
    private Long trackUserDeptId;
    /**
     * 跟单员部门名称
     */
    private String trackUserDeptName;
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
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 箱数
     */
    private Integer boxCount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 处理标识:1-返工重验 1-让步放行
     */
    private Integer handleFlag;

    /**
     * 返工说明
     */
    private String reworkDesc;

    /**
     * 采购数量
     */
    private Integer purchaseQuantity;

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
     * 包装方式
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;

    /**
     * 主图
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;

    /**
     * 基础产品编号
     */
    private String basicSkuCode;
}
