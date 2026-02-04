package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/12 10:47
 */
@Data
public class PushOytShipmentPlanItem {

    @Schema(description = "内部法人单位主键", example = "4829")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String companyName;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "条形码")
    private String barcode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "报关数量")
    private Integer declarationQuantity;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "销售单价")
    private JsonAmount saleUnitPrice;

    @Schema(description = "销售金额")
    private JsonAmount saleAmount;

    @Schema(description = "报关单价")
    private JsonAmount declarationUnitPrice;

    @Schema(description = "报关金额")
    private JsonAmount declarationAmount;

    @Schema(description = "商检类型")
    private Integer commodityInspectionType;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "报关中文品名")
    private String declarationName;

    @Schema(description = "报关英文品名")
    private String declarationNameEng;

    @Schema(description = "计量单位")
    private String hsMeasureUnit;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "退税金额")
    private JsonAmount taxRefundPrice;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "采购单价")
    private JsonAmount purchaseUnitPrice;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "包装方式名称")
    private String packageTypeName;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "总体积")
    private BigDecimal totalVolume;

    @Schema(description = "总净重")
    private JsonWeight totalNetweight;

    @Schema(description = "总毛重")
    private JsonWeight totalGrossweight;

    @Schema(description = "中文说明")
    private String description;

    @Schema(description = "英文说明")
    private String descriptionEng;

    @Schema(description = "仓库编号")
    private String stockCode;

    @Schema(description = "仓库名称")
    private String stockName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "佣金金额")
    private JsonAmount commissionAmount;

    @Schema(description = "毛利率")
    private BigDecimal orderGrossProfitRate;

    @Schema(description = "是否出仓")
    private Integer outboundFlag;

    @Schema(description = "出仓日期")
    private LocalDateTime outboundDate;

    @Schema(description = "报关单位")
    private String unit;

    @Schema(description = "已报关数")
    private Integer declaredQuantity;

    @Schema(description = "是否转结汇单")
    private Integer settleOrderFlag;

    @Schema(description = "外销合同编号")
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "收款方式主键")
    private Long settlementId;

    @Schema(description = "收款方式名称")
    private String settlementName;

    @Schema(description = "价格条款")
    private String settlementTermType;

    @Schema(description = "发货地点")
    private Integer shippedAddress;

    @Schema(description = "交货日期")
    private LocalDateTime deliveryDate;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;

    @Schema(description = "跟单员")
    private UserDept manager;

    @Schema(description = "佣金类型")
    private Integer commissionType;

    @Schema(description = "佣金比例")
    private BigDecimal commissionRate;

    @Schema(description = "包装价")
    private JsonAmount purchasePackagingPrice;

    @Schema(description = "运费")
    private JsonAmount purchaseShippingPrice;

    @Schema(description = "含税总价")
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "销售数量")
    private Integer saleQuantity;

    @Schema(description = "出运数量")
    private Integer shippingQuantity;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应商code")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "应付供应商id")
    private Long payVenderId;

    @Schema(description = "应付供应商code")
    private String payVenderCode;

    @Schema(description = "应付供应商名称")
    private String payVenderName;

    /**
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 是否商检
     */
    private Integer commodityInspectionFlag;

    /**
     * 已出运数
     */
    @CompareField(value = "已出运数")
    private Integer shippedQuantity;

    /**
     * 未出运数
     */
    @CompareField(value = "未出运数")
    private Integer unshippedQuantity;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept sales;

    /**
     * 真实锁定数量
     */
    private Integer realLockQuantity;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 单位
     */
    private Integer measureUnit;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 图片
     */
    private SimpleFile mainPicture;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 库存采购合同号
     */
    private List<String> stockPurchaseContractCodes;
}
