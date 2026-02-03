package com.syj.eplus.module.dms.enums.api.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipmentItemDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 出运单主键
     */
    private Long shipmentId;
    /**
     * 发货地点
     */
    private Integer shippedAddress;

    /**
     * 预计拉柜时间
     */
    private LocalDateTime estPickupTime;

    /**
     * 产品主键
     */
    private Long skuId;

    /**
     * 产品编号
     */
    private String skuCode;

    /**
     * 客户货号
     */
    private String cskuCode;


    /**
     * 基础产品编号
     */
    private String basicSkuCode;

    /**
     * 中文名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String nameEng;

    /**
     * 报关数量
     */
    private Integer declarationQuantity;

    /**
     * 已报关数量
     */
    private Integer declarationQuantityOld;

    /**
     * 采购数量
     */
    private Integer purchaseTotalQuantity;

    /**
     * 销售单价
     */
    private JsonAmount saleUnitPrice;

    /**
     * 销售金额
     */
    private JsonAmount saleAmount;

    /**
     * 报关单价
     */
    private JsonAmount declarationUnitPrice;

    /**
     * 报关金额
     */
    private JsonAmount declarationAmount;

    /**
     * 保险费
     */
    private JsonAmount insuranceFee;

    /**
     * 交货日期
     */
    private LocalDateTime deliveryDate;

    /**
     * 是否商检
     */
    private Integer commodityInspectionFlag;

    /**
     * 商检类型
     */
    private Integer commodityInspectionType;

    /**
     * HS编码
     */
    private String hsCode;

    /**
     * 报关中文品名
     */
    private String declarationName;

    /**
     * 报关英文品名
     */
    private String customsDeclarationNameEng;

    /**
     * 海关计量单位
     */
    private String hsMeasureUnit;

    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    private JsonAmount taxRefundPrice;

    /**
     * 采购合同编号
     */
    private String purchaseContractCode;

    /**
     * 采购员id
     */
    private Long purchaseUserId;

    /**
     * 采购员姓名
     */
    private String purchaseUserName;

    /**
     * 采购员部门id
     */
    private Long purchaseUserDeptId;

    /**
     * 采购员部门名称
     */
    private String purchaseUserDeptName;

    /**
     * 采购币种
     */
    private String purchaseCurrency;

    /**
     * 采购单价
     */
    private JsonAmount purchaseUnitPrice;

    /**
     * 包装方式
     */
    private List<Long> packageType;

    private String packageTypeName;

    private String packageTypeEngName;

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
     * 总体积
     */
    private BigDecimal totalVolume;

    /**
     * 总净重
     */
    private JsonWeight totalNetweight;

    /**
     * 总毛重
     */
    private JsonWeight totalGrossweight;

    /**
     * 中文说明
     */
    private String description;
    /**
     * 英文说明
     */
    private String descriptionEng;

    /**
     * 仓库编号
     */
    private String stockCode;

    /**
     * 仓库名称
     */
    private String stockName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 佣金金额
     */
    private JsonAmount commissionAmount;

    /**
     * 毛利率
     */
    private BigDecimal orderGrossProfitRate;

    /**
     * 是否出仓
     */
    private Integer outboundFlag;

    /**
     * 出仓日期
     */
    private LocalDateTime outboundDate;

    /**
     * 报关单位
     */
    private String declarationUnit;

    /**
     * 已报关数
     */
    private Integer declaredQuantity;

    /**
     * 是否转结汇单
     */
    private Integer settleOrderFlag;

    /**
     * 价格条款
     */
    private String settlementTermType;

    /**
     * 正面唛头
     */
    private String frontShippingMark;

    /**
     * 侧面唛头
     */
    private String sideShippingMark;

    private String receivePerson;

    private String notifyPerson;

    private Long custId;

    private String custCode;

    private String custName;

    private String saleContractCode;

    /**
     * 是否转报关单
     */
    private Integer isToDeclaration;

    /**
     * 转报关单人
     */
    private UserDept declarationUser;

    /**
     * 转报关单时间
     */
    private LocalDateTime declarationDate;

    /**
     * 是否转结汇单
     */
    private Integer isToSettlementForm;

    /**
     * 转报结汇单人
     */
    private UserDept settlementFormUser;

    /**
     * 转结汇单时间
     */
    private LocalDateTime settlementFormDate;

    /**
     * 是否转商检单
     */
    private Integer isToCommodityInspection;

    /**
     * 转商检单人
     */
    private UserDept commodityInspectionUser;

    /**
     * 转结汇单时间
     */
    private LocalDateTime commodityInspectionDate;

    private UserDept manager;

    /**
     * 出运数量
     */
    private Integer shippingQuantity;

    /**
     * 供应商名称
     */
    private String venderName;

    /**
     * 供应商id
     */
    private Long venderId;

    /**
     * 供应商code
     */
    private String venderCode;

    /**
     * 供应商名称
     */
    private String payVenderName;

    /**
     * 供应商id
     */
    private Long payVenderId;

    /**
     * 供应商code
     */
    private String payVenderCode;
    /**
     * 交易币别
     */
    private String currency;

    /**
     * 结汇方式
     */
    private Long settlementId;

    /**
     * 结汇名称
     */
    private String settlementName;

    /**
     * 状态
     */
    private Integer status;

    private Long saleContractItemId;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;


    /**
     * 船代费用均摊金额
     */
    private JsonAmount forwarderShareAmount;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 结汇数量
     */
    private Integer settlementQuantity;

    /**
     * 开票数量
     */
    private BigDecimal invoicedQuantity;

    /**
     * 本次采购数量
     */
    private BigDecimal thisPurchaseQuantity;

    /**
     * 转开票状态
     */
    private Integer invoiceStatus;

    /**
     * 转拉柜通知单标识 0-否 1-是
     */
    private Integer converNoticeFlag;


    private JsonAmount purchaseWithTaxPrice;

    /**
     * 来源销售合同编号
     */
    private String sourceSaleContractCode;


    /**
     * 外销员
     */
    private UserDept sales;



    /**
     * 拆分采购标记
     */
    private Integer purchaseModel;

    /**
     * 出库数量
     */
    private Integer outQuantity;

    /**
     * 出库日期
     */
    private LocalDateTime outDate;

    /**
     * 主体id列表
     */
    private List<Long> companyIdList;

    /**
     * 真实锁定数量
     */
    private Integer realLockQuantity;

    private Integer skuDeletedFlag;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 单位
     */
    private Integer measureUnit;

    /**
     * 客户PO号
     */
    private String custPo;

    /**
     * 库存成本
     */
    private JsonAmount stockCost;

    /**
     * 验货状态
     */
    private Integer checkStatus;


    private JsonCompanyPath companyPath;

    /**
     * 规格
     */
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}
