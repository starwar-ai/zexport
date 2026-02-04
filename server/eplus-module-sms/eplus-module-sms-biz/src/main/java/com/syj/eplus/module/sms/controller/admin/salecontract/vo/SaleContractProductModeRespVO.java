package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.entity.JsonLock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品模式扁平响应 VO（合并主表和明细字段）
 * 用于产品视图的扁平化展示，按明细分页返回数据
 *
 * @author 波波
 */
@Schema(description = "管理后台 - 外销合同产品模式 Response VO")
@Data
@Accessors(chain = true)
public class SaleContractProductModeRespVO {

    // ===== 主表字段（来自 SaleContractDO / SaleContractRespVO） =====

    @Schema(description = "明细主键（用于前端表格唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "合同主键")
    private Long contractId;
    
    @Schema(description = "合同编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "内部法人单位主键")
    private Long companyId;

    @Schema(description = "内部法人单位名称")
    private String companyName;

    @Schema(description = "外贸公司单位主键")
    private Long foreignTradeCompanyId;

    @Schema(description = "外贸公司单位名称")
    private String foreignTradeCompanyName;

    @Schema(description = "订单路径")
    private JsonCompanyPath companyPath;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "交易币别")
    private String currency;

    @Schema(description = "价格条款")
    private String settlementTermType;

    @Schema(description = "收款方式主键")
    private Long settlementId;

    @Schema(description = "收款方式名称")
    private String settlementName;

    @Schema(description = "客户国别主键")
    private Long custCountryId;

    @Schema(description = "客户国别")
    private String custCountryName;

    @Schema(description = "客户合同号")
    private String custPo;

    @Schema(description = "是否代理")
    private Integer agentFlag;

    @Schema(description = "销售人员")
    private UserDept sales;

    @Schema(description = "业务员昵称（扁平化字段）")
    private String salesNickname;

    @Schema(description = "业务员部门名称（扁平化字段）")
    private String salesDeptName;

    @Schema(description = "录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "客户交期")
    private LocalDateTime custDeliveryDate;

    @Schema(description = "销售合同类型")
    private Integer saleType;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建人部门")
    private String creatorDeptName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "单据状态")
    private Integer auditStatus;

    @Schema(description = "合同状态")
    private Integer status;

    @Schema(description = "回签日期")
    private LocalDateTime signBackDate;

    @Schema(description = "创建时汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "打印状态")
    private Integer printFlag;

    // ===== 明细字段（来自 SaleContractItem） =====

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "销售单价")
    private JsonAmount unitPrice;

    @Schema(description = "外销总金额")
    private JsonAmount totalSaleAmount;

    @Schema(description = "外销总金额USD")
    private JsonAmount totalSaleAmountUsd;

    @Schema(description = "采购单价")
    private JsonAmount purchaseUnitPrice;

    @Schema(description = "真实采购单价")
    private JsonAmount realPurchaseWithTaxPrice;

    @Schema(description = "含税单价")
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "含税含包装单价")
    private JsonAmount purchaseTotalPrice;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应商code")
    private String venderCode;

    @Schema(description = "库存")
    private Integer inventoryQuantity;

    @Schema(description = "锁定数量")
    private Integer currentLockQuantity;

    @Schema(description = "真实锁定数量")
    private Integer realLockQuantity;

    @Schema(description = "真实采购数量")
    private Integer realPurchaseQuantity;

    @Schema(description = "待采购数量")
    private Integer needPurQuantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "订单毛利")
    private JsonAmount orderGrossProfit;

    @Schema(description = "毛利率")
    private BigDecimal orderGrossProfitRate;

    @Schema(description = "工厂交期")
    private LocalDateTime venderDeliveryDate;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "体积")
    private BigDecimal volume;

    @Schema(description = "是否翻单")
    private Integer reorderFlag;

    @Schema(description = "海关编码")
    private String hsCode;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "退税金额")
    private JsonAmount taxRefundPrice;

    @Schema(description = "明细状态")
    private Integer itemStatus;

    @Schema(description = "采购员")
    private UserDept purchaseUser;

    @Schema(description = "采购员昵称（扁平化字段）")
    private String purchaseUserNickname;

    @Schema(description = "采购员部门名称（扁平化字段）")
    private String purchaseUserDeptName;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "产品类型")
    private Integer skuType;

    @Schema(description = "是否自营")
    private Integer ownBrandFlag;

    @Schema(description = "已出运数")
    private Integer shippedQuantity;

    @Schema(description = "已转出运数")
    private Integer transferShippedQuantity;

    @Schema(description = "入库状态")
    private Integer billStatus;

    @Schema(description = "已入库数量")
    private Integer billQuantity;

    @Schema(description = "跟单员")
    private UserDept manager;

    @Schema(description = "跟单员昵称（扁平化字段）")
    private String managerNickname;

    @Schema(description = "跟单员部门名称（扁平化字段）")
    private String managerDeptName;

    @Schema(description = "不包含赠品采购价")
    private JsonAmount withTaxPriceRemoveFree;

    @Schema(description = "内部法人单位id列表（用于锁库）")
    private List<Long> companyIdList;

    @Schema(description = "锁库信息（用于计算锁库单价和总价）")
    private List<JsonLock> lockMsg;

    @Schema(description = "锁库单价")
    private JsonAmount stockLockPrice;

    @Schema(description = "锁库总价")
    private JsonAmount stockLockTotalPrice;
}
