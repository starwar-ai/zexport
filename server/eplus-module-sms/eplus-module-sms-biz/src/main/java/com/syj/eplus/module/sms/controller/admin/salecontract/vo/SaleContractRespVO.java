package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.*;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryContractItemDTO;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外销合同 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class SaleContractRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13247")
    @ChangeIgnore
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    @ChangeIgnore
    private String code;

    @Schema(description = "确认状态")
    @ExcelProperty("确认状态")
    @ExcelIgnore
    @ChangeIgnore
    private Integer confirmFlag;

    @Schema(description = "内部法人单位主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    @ExcelProperty(value = "内部法人单位")
    private String companyName;
    @Schema(description = "外贸公司单位主键")
    private Long foreignTradeCompanyId;

    @Schema(description = "外贸公司单位名称")
    private String foreignTradeCompanyName;
    @Schema(description = "订单路径", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("订单路径")
    private JsonCompanyPath companyPath;

    @Schema(description = "客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "客户")
    private String custName;

    @Schema(description = "内部客户主键")
    private Long internalCustId;

    @Schema(description = "内部客户编号")
    private String internalCustCode;

    @Schema(description = "内部客户名称")
    private String internalCustName;

    @Schema(description = "交易币别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("交易币别")
    private String currency;

    @Schema(description = "价格条款", example = "1")
    @ExcelProperty("价格条款")
    private String settlementTermType;

    @Schema(description = "收款方式主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long settlementId;

    @Schema(description = "收款方式名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "收款方式")
    private String settlementName;

    @Schema(description = "客户国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private Long custCountryId;

    @Schema(description = "客户国别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "客户国别")
    @ChangeIgnore
    private String custCountryName;

    @Schema(description = "客户区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private String custAreaName;

    @Schema(description = "客户合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户合同号")
    private String custPo;

    @Schema(description = "是否代理", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "是否代理", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer agentFlag;

    @Schema(description = "应收客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long collectedCustId;

    @Schema(description = "应收客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collectedCustCode;

    @Schema(description = "应收客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "应收客户")
    private String collectedCustName;

    @Schema(description = "收货客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long receiveCustId;

    @Schema(description = "收货客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiveCustCode;

    @Schema(description = "收货客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "收货客户")
    private String receiveCustName;

    @Schema(description = "销售人员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "销售人员", converter = UserDeptConverter.class)
    private UserDept sales;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "录入日期")
    @ChangeIgnore
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "贸易国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "贸易国别")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryArea;

    @Schema(description = "出运国主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "出运国")
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "出运口岸")
    private String departurePortName;

    @Schema(description = "目的口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "目的口岸")
    private String destinationPortName;

    @Schema(description = "运输方式", example = "1")
    @ExcelProperty("运输方式")
    private Integer transportType;

    @Schema(description = "客户交期")
    @ExcelProperty("客户交期")
    private LocalDateTime custDeliveryDate;

    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("20尺柜")
    @ChangeIgnore
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺柜")
    @ChangeIgnore
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺高柜")
    @ChangeIgnore
    private Integer fortyFootContainerNum;

    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "散货", converter = VolumeConvert.class)
    @ChangeIgnore
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "拖柜费", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount trailerFee;

    @Schema(description = "拖柜费金额")
    @ExcelProperty(value = "拖柜费")
    @ChangeIgnore
    private BigDecimal trailerFeeAmount;

    @Schema(description = "拖柜费币种")
    @ExcelProperty(value = "拖柜费币种")
    @ChangeIgnore
    private String trailerFeeCurrency;

    @Schema(description = "预估总运费", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount estimatedTotalFreight;

    @Schema(description = "预估总运费金额")
    @ExcelProperty(value = "预估总运费")
    @ChangeIgnore
    private BigDecimal estimatedTotalFreightAmount;

    @Schema(description = "预估总运费币种")
    @ExcelProperty(value = "预估总运费币种")
    @ChangeIgnore
    private String estimatedTotalFreightCurrency;

    @Schema(description = "是否订舱", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "是否订舱", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    @ChangeIgnore
    private Integer bookingFlag;

    @Schema(description = "佣金", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount commission;

    @Schema(description = "佣金金额")
    @ExcelProperty(value = "佣金")
    @ChangeIgnore
    private BigDecimal commissionAmount;

    @Schema(description = "佣金币种")
    @ExcelProperty(value = "佣金币种")
    @ChangeIgnore
    private String commissionCurrency;

    @Schema(description = "平台费", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount platformFee;

    @Schema(description = "平台费金额")
    @ExcelProperty(value = "平台费")
    @ChangeIgnore
    private BigDecimal platformFeeAmount;

    @Schema(description = "平台费币种")
    @ExcelProperty(value = "平台费币种")
    @ChangeIgnore
    private String platformFeeCurrency;

    @Schema(description = "保险费", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount insuranceFee;

    @Schema(description = "保险费金额")
    @ExcelProperty(value = "保险费")
    @ChangeIgnore
    private BigDecimal insuranceFeeAmount;

    @Schema(description = "保险费币种")
    @ExcelProperty(value = "保险费币种")
    @ChangeIgnore
    private String insuranceFeeCurrency;

    @Schema(description = "中信保费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount sinosureFee;

    @Schema(description = "中信保费用金额")
    @ExcelProperty(value = "中信保费用")
    @ChangeIgnore
    private BigDecimal sinosureFeeAmount;

    @Schema(description = "中信保费用币种")
    @ExcelProperty(value = "中信保费用币种")
    @ChangeIgnore
    private String sinosureFeeCurrency;

    @Schema(description = "包干费", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount lumpSumFee;

    @Schema(description = "包干费金额")
    @ExcelProperty(value = "包干费")
    @ChangeIgnore
    private BigDecimal lumpSumFeeAmount;

    @Schema(description = "包干费币种")
    @ExcelProperty(value = "包干费币种")
    @ChangeIgnore
    private String lumpSumFeeCurrency;

    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount additionAmount;

    @Schema(description = "加项金额金额")
    @ExcelProperty(value = "加项金额")
    @ChangeIgnore
    private BigDecimal additionAmountAmount;

    @Schema(description = "加项金额币种")
    @ExcelProperty(value = "加项金额币种")
    @ChangeIgnore
    private String additionAmountCurrency;

    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount deductionAmount;

    @Schema(description = "减项金额金额")
    @ExcelProperty(value = "减项金额")
    @ChangeIgnore
    private BigDecimal deductionAmountAmount;

    @Schema(description = "减项金额币种")
    @ExcelProperty(value = "减项金额币种")
    @ChangeIgnore
    private String deductionAmountCurrency;

    @Schema(description = "验货费用", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount inspectionFee;

    @Schema(description = "验货费用金额")
    @ExcelProperty(value = "验货费用")
    @ChangeIgnore
    private BigDecimal inspectionFeeAmount;

    @Schema(description = "验货费用币种")
    @ExcelProperty(value = "验货费用币种")
    @ChangeIgnore
    private String inspectionFeeCurrency;

    @Schema(description = "预计包材合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount estimatedPackingMaterials;

    @Schema(description = "预计包材合计金额")
    @ExcelProperty(value = "预计包材合计")
    @ChangeIgnore
    private BigDecimal estimatedPackingMaterialsAmount;

    @Schema(description = "预计包材合计币种")
    @ExcelProperty(value = "预计包材合计币种")
    @ChangeIgnore
    private String estimatedPackingMaterialsCurrency;

    @Schema(description = "配件采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount accessoriesPurchaseTotal;

    @Schema(description = "配件采购合计金额")
    @ExcelProperty(value = "配件采购合计")
    @ChangeIgnore
    private BigDecimal accessoriesPurchaseTotalAmount;

    @Schema(description = "配件采购合计币种")
    @ExcelProperty(value = "配件采购合计币种")
    @ChangeIgnore
    private String accessoriesPurchaseTotalCurrency;

    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("箱数合计")
    @ChangeIgnore
    private Integer totalBoxes;

    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "毛重合计", converter = WeightConvert.class)
    @ChangeIgnore
    private JsonWeight totalGrossweight;

    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "净重合计", converter = WeightConvert.class)
    @ChangeIgnore
    private JsonWeight totalWeight;

    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "体积合计", converter = VolumeConvert.class)
    @ChangeIgnore
    private BigDecimal totalVolume;

    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount totalGoodsValue;

    @Schema(description = "货值合计金额")
    @ExcelProperty(value = "货值合计")
    @ChangeIgnore
    private BigDecimal totalGoodsValueAmount;

    @Schema(description = "货值合计币种")
    @ExcelProperty(value = "货值合计币种")
    @ChangeIgnore
    private String totalGoodsValueCurrency;

    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "采购合计", converter = AmountListConvert.class)
    @ChangeIgnore
    private List<JsonAmount> totalPurchase;

    @Schema(description = "退税合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount totalVatRefund;

    @Schema(description = "退税合计金额")
    @ExcelProperty(value = "退税合计")
    @ChangeIgnore
    private BigDecimal totalVatRefundAmount;

    @Schema(description = "退税合计币种")
    @ExcelProperty(value = "退税合计币种")
    @ChangeIgnore
    private String totalVatRefundCurrency;

    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("数量合计")
    @ChangeIgnore
    private Integer totalQuantity;

    @Schema(description = "订单毛利", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount orderGrossProfit;

    @Schema(description = "订单毛利金额")
    @ExcelProperty(value = "订单毛利")
    @ChangeIgnore
    private BigDecimal orderGrossProfitAmount;

    @Schema(description = "订单毛利币种")
    @ExcelProperty(value = "订单毛利币种")
    @ChangeIgnore
    private String orderGrossProfitCurrency;

    @Schema(description = "毛利率", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("毛利率")
    @ChangeIgnore
    private BigDecimal grossProfitMargin;

    @Schema(description = "应收汇款", requiredMode = Schema.RequiredMode.REQUIRED)
    @ChangeIgnore
    private JsonAmount receivableExchange;

    @Schema(description = "应收汇款金额")
    @ExcelProperty(value = "应收汇款")
    @ChangeIgnore
    private BigDecimal receivableExchangeAmount;

    @Schema(description = "应收汇款币种")
    @ExcelProperty(value = "应收汇款币种")
    @ChangeIgnore
    private String receivableExchangeCurrency;

    @Schema(description = "销售合同类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("销售合同类型")
    @ChangeIgnore
    private Integer saleType;

    @Schema(description = "流程id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ChangeIgnore
    private String processInstanceId;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建人部门")
    private String creatorDeptName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "应收金额")
    @ChangeIgnore
    private JsonAmount receivableAmount;

    @Schema(description = "实收金额")
    @ChangeIgnore
    private JsonAmount receivedAmount;

    @Schema(description = "单据状态")
    @ChangeIgnore
    private Integer auditStatus;

    @Schema(description = "加减项列表")
    @ChangeIgnore
    private List<AddSubItem> addSubItemList;

    @Schema(description = "收款计划")
    @ChangeIgnore
    private List<CollectionPlan> collectionPlanList;

    @Schema(description = "辅料分摊")
    @ChangeIgnore
    private List<AuxiliaryContractItemDTO> AuxiliaryPurchaseItemList;

    @Schema(description = "销售明细")
    @ChangeIgnore
    private List<SaleContractItem> children;

    @Schema(description = "操作日志")
    @ChangeIgnore
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "回签人")
    @ChangeIgnore
    private UserDept signBackUser;

    @Schema(description = "回签日期")
    @ChangeIgnore
    private LocalDateTime signBackDate;

    @Schema(description = "状态")
    @ChangeIgnore
    private Integer status;

    @Schema(description = "变更状态")
    @ChangeIgnore
    private Integer changeStatus;

    @Schema(description = "自动生成标识 0-否 1-是")
    @ChangeIgnore
    private Integer autoFlag;

    @Schema(description = "订单链路")
    @ChangeIgnore
    List<OrderLinkDTO> orderLingDTO;

    @Schema(description = "设计稿")
    @ChangeIgnore
    private List<SimpleFile> designDraftList;

    @Schema(description = "回签附件必填标记")
    @ChangeIgnore
    private Integer signBackAnnexNotNullFlag;

    @Schema(description = "回签标记")
    @ChangeIgnore
    private Integer signBackFlag;

    @Schema(description = "回签附件")
    private List<SimpleFile> signBackAnnex;

    @Schema(description = "备注")
    private String signBackDesc;

    @Schema(description = "链路编号")
    @ChangeIgnore
    private List<String> linkCodeList;

    @Schema(description = "创建时汇率")
    @ChangeIgnore
    private BigDecimal exchangeRate;


    @Schema(description = "客户收款账号id")
    @ChangeIgnore
    private Long collectionAccountId;

    @Schema(description = "客户收款账号")
    @ChangeIgnore
    private String collectionAccountBankCode;

    @Schema(description = "来源销售合同主键")
    @ChangeIgnore
    private Long sourceContractId;

    @Schema(description = "来源销售合同编码")
    @ChangeIgnore
    private String sourceContractCode;

    @Schema(description = "送货地址")
    @ChangeIgnore
    private String deliveryAddress;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "销售总金额")
    private JsonAmount totalAmount;

    @Schema(description = "销售总金额金额")
    @ExcelProperty(value = "销售总金额")
    @ChangeIgnore
    private BigDecimal totalAmountAmount;

    @Schema(description = "销售总金额币种")
    @ExcelProperty(value = "销售总金额币种")
    @ChangeIgnore
    private String totalAmountCurrency;

    @Schema(description = "销售总金额USD")
    private JsonAmount totalAmountUsd;

    @Schema(description = "销售总金额USD金额")
    @ExcelProperty(value = "销售总金额USD")
    @ChangeIgnore
    private BigDecimal totalAmountUsdAmount;

    @Schema(description = "销售总金额USD币种")
    @ExcelProperty(value = "销售总金额USD币种")
    @ChangeIgnore
    private String totalAmountUsdCurrency;
    
    private List<JsonAmount> summary;

    @Schema(description = "汇总销售明细")
    @ChangeIgnore
    private List<SaleContractItem> groupChildren;

    @Schema(description = "真实订单毛利")
    private JsonAmount realOrderGrossProfit;

    @Schema(description = "真实毛利率")
    private BigDecimal realGrossProfitMargin;

    @Schema(description = "真实采购合计")
    private JsonAmount realPurchaseTotal;

    @Schema(description = "真实体积合计")
    @ExcelProperty(value = "真实体积合计", converter = VolumeConvert.class)
    @ChangeIgnore
    private BigDecimal realTotalVolume;

    @Schema(description = "真实20尺柜")
    private Integer realTwentyFootCabinetNum;

    @Schema(description = "真实40尺柜")
    private Integer realFortyFootCabinetNum;

    @Schema(description = "真实40尺高柜")
    private Integer realFortyFootContainerNum;

    @Schema(description = "真实散货")
    @ExcelProperty(value = "真实散货", converter = VolumeConvert.class)
    @ChangeIgnore
    private BigDecimal realBulkHandlingVolume;

    @Schema(description = "真实预估总运费")
    private JsonAmount realEstimatedTotalFreight;

    @Schema(description = "真实箱数合计")
    private Integer realTotalBoxes;

    @Schema(description = "真实毛重合计")
    private JsonWeight realTotalGrossweight;

    @Schema(description = "真实净重合计")
    private JsonWeight realTotalWeight;

    @Schema(description = "真实退税合计")
    private JsonAmount realTotalVatRefund;

    @Schema(description = "真实配件采购合计")
    private JsonAmount realAccessoriesPurchaseTotal;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "剩余未收金额")
    private JsonAmount unReceivedAmount;

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
