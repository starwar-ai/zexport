package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外销合同新增/修改 Request VO")
@Data
public class SaleContractSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13247")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "内部法人单位主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    private String companyName;

    @Schema(description = "订单路径", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "订单路径不能为空")
    private JsonCompanyPath companyPath;

    @Schema(description = "客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custName;

    @Schema(description = "内部客户主键")
    private Long internalCustId;

    @Schema(description = "内部客户编号")
    private String internalCustCode;

    @Schema(description = "内部客户名称")
    private String internalCustName;

    @Schema(description = "交易币别", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "交易币别不能为空")
    private String currency;

    @Schema(description = "价格条款", example = "1")
    private String settlementTermType;

    @Schema(description = "收款方式主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long settlementId;

    @Schema(description = "收款方式名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String settlementName;

    @Schema(description = "客户国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long custCountryId;

    @Schema(description = "客户国别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCountryName;

    @Schema(description = "客户区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custAreaName;

    @Schema(description = "客户合同号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "客户合同号不能为空")
    private String custPo;

    @Schema(description = "是否代理", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "是否代理不能为空")
    private Integer agentFlag;

    @Schema(description = "应收客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long collectedCustId;

    @Schema(description = "应收客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collectedCustCode;

    @Schema(description = "应收客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collectedCustName;

    @Schema(description = "收货客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long receiveCustId;

    @Schema(description = "收货客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiveCustCode;

    @Schema(description = "收货客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiveCustName;

    @Schema(description = "销售人员", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "销售人员不能为空")
    private UserDept sales;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "贸易国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryName;

    @Schema(description = "贸易国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryArea;

    @Schema(description = "出运国主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "出运口岸主键不能为0")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departurePortName;

    @Schema(description = "目的口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "目的口岸主键不能为0")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destinationPortName;

    @Schema(description = "运输方式", example = "1")
    private Integer transportType;

    @Schema(description = "客户交期")
    private LocalDateTime custDeliveryDate;

    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "20尺柜不能为空")
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "40尺柜不能为空")
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "40尺高柜不能为空")
    private Integer fortyFootContainerNum;

    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "散货不能为空")
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "拖柜费", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "拖柜费不能为空")
    private JsonAmount trailerFee;

    @Schema(description = "预估总运费", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "预估总运费不能为空")
    private JsonAmount estimatedTotalFreight;

    @Schema(description = "是否订舱", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "是否订舱不能为空")
    private Integer bookingFlag;

    @Schema(description = "佣金", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "佣金不能为空")
    private JsonAmount commission;

    @Schema(description = "平台费", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "平台费不能为空")
    private JsonAmount platformFee;

    @Schema(description = "保险费", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "保险费不能为空")
    private JsonAmount insuranceFee;

    @Schema(description = "中信保费用", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "中信保费用不能为空")
    private JsonAmount sinosureFee;

    @Schema(description = "包干费", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount lumpSumFee;

    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "加项金额不能为空")
    private JsonAmount additionAmount;

    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "减项金额不能为空")
    private JsonAmount deductionAmount;

    @Schema(description = "验货费用", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "验货费用不能为空")
    private JsonAmount inspectionFee;

    @Schema(description = "预计包材合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "预计包材合计不能为空")
    private JsonAmount estimatedPackingMaterials;

    @Schema(description = "配件采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "配件采购合计不能为空")
    private JsonAmount accessoriesPurchaseTotal;

    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "箱数合计不能为空")
    private Integer totalBoxes;

    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "毛重合计不能为空")
    private JsonWeight totalGrossweight;

    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "净重合计不能为空")
    private JsonWeight totalWeight;

    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "体积合计不能为空")
    private BigDecimal totalVolume;

    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "货值合计不能为空")
    private JsonAmount totalGoodsValue;

    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "采购合计不能为空")
    private List<JsonAmount> totalPurchase;

    @Schema(description = "退税合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "退税合计不能为空")
    private JsonAmount totalVatRefund;

    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "数量合计不能为空")
    private Integer totalQuantity;

    @Schema(description = "订单毛利", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "订单毛利不能为空")
    private JsonAmount orderGrossProfit;

    @Schema(description = "毛利率", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "毛利率不能为空")
    private BigDecimal grossProfitMargin;

    @Schema(description = "应收汇款", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "应收汇款不能为空")
    private JsonAmount receivableExchange;

    @Schema(description = "销售合同类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "销售合同类型不能为空")
    private Integer saleType;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "加减项列表")
    private List<AddSubItem> addSubItemList;

    @Schema(description = "收款方式")
    private List<CollectionPlan> collectionPlanList;

    @Schema(description = "销售明细")
    private List<SaleContractItem> children;


    @Schema(description = "自动生成标识 0-否 1-是")
    private Integer autoFlag;

    @Schema(description = "设计稿")
    private List<SimpleFile> designDraftList;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    @Schema(description = "校验标识")
    private Boolean checkFlag = Boolean.TRUE;


    @Schema(description = "创建时汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "客户收款账号id")
    private Long collectionAccountId;

    @Schema(description = "客户收款账号")
    private String collectionAccountBankCode;

    @Schema(description = "来源销售合同主键")
    private Long sourceContractId;

    @Schema(description = "来源销售合同编码")
    private String sourceContractCode;

    @Schema(description = "送货地址")
    private String deliveryAddress;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "销售总金额")
    private JsonAmount totalAmount;

    @Schema(description = "销售总金额USD")
    private JsonAmount totalAmountUsd;

    private String genContractUniqueCode;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "美元汇率")
    private BigDecimal usdRate;

    @Schema(description = "销售合同日期")
    private LocalDateTime saleContractDate;

    @Schema(description = "库存成本合计")
    private JsonAmount totalStockCost;

    @Schema(description = "收款合计")
    private JsonAmount collectionTotal;

    @Schema(description = "跟单员")
    private UserDept manager;

    @Schema(description = "销售总金额(原币种)")
    private JsonAmount totalAmountThisCurrency;


    @Schema(description = "备注")
    private String remark;
}
