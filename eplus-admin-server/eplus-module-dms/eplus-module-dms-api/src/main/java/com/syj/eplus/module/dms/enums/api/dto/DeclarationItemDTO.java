package com.syj.eplus.module.dms.enums.api.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeclarationItemDTO {

   /**
    * 主键
    */
   private Long id;

   private String saleContractCode;

   /**
    * 出运明细明细id
    */
   private Long shipmentItemId;

   /**
    * 海关编码
    */
   private String hsCode;
   /**
    * 报关单Id
    */
   private Long declarationId;

   /**
    * 报关要素
    */
   private String declarationElement;
   /**
    * 中文名称
    */
   private String declarationName;
   /**
    * 英文名称
    */
   private String customsDeclarationNameEng;
   /**
    * 客户货号
    */
   private String cskuCode;

   /**
    * 海关计量单位
    */
   private String hsMeasureUnit;

   /**
    * 报关数量
    */
   private Integer declarationQuantity;
   /**
    * 箱数
    */
   private Integer boxCount;
   /**
    * 计价方式
    */
   private Integer pricingMethod;
   /**
    * 报关单据
    */
   private String declarationInvoices;
   /**
    * 报关总价
    */
   private BigDecimal declarationTotalPrice;
   /**
    * 总体积
    */
   private BigDecimal totalVolume;

   private JsonWeight totalGrossweight;

   private BigDecimal taxRefundRate;

   private String frontShippingMark;

   private String sideShippingMark;

   private String receivePerson;

   private String notifyPerson;

   private Long custId;

   private String custCode;

   private String custName;

   /**
    * 外销币种
    */
   private String currency;

   private Long settlementId;

   private String settlementName;

   /**
    * 条形码
    *
    */
   private String barcode;

   /**
    * 价格条款
    */
   private String settlementTermType;

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
    * 中文名称
    */
   private String name;
   /**
    * 英文名称
    */
   private String nameEng;

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
    * 总净重
    */
   private JsonWeight totalNetweight;

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
   private String venderId;

   /**
    * 供应商code
    */
   private String venderCode;

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
