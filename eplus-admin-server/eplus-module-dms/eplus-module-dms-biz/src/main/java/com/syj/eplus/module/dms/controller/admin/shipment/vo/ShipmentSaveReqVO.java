package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.temporarysku.TemporarySku;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出运单新增/修改 Request VO")
@Data
public class ShipmentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16034")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shipmentPlanCode;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "客户主键", example = "12793")
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

//    @Schema(description = "外销合同", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String saleContractCode;

    @Schema(description = "客户合同", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custContractCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String custName;

    @Schema(description = "发票号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String invoiceCode;

    @Schema(description = "价格条款", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private String settlementTermType;

    @Schema(description = "预计出运")
    private LocalDateTime estShipDate;

    @Schema(description = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "单据员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept orderManager;

    @Schema(description = "运输方式", example = "1")
    private Integer transportType;

    @Schema(description = "外销员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept sales;

    @Schema(description = "出运国主键", example = "6887")
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", example = "29501")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String departurePortName;

    @Schema(description = "贸易国别主键", example = "2970")
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryArea;

    @Schema(description = "目的口岸主键", example = "30271")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String destinationPortName;

    @Schema(description = "贸易方式", example = "1")
    private Integer tradeType;

    @Schema(description = "是否出仓")
    private Integer outboundFlag;

    @Schema(description = "出仓日期")
    private LocalDateTime outboundDate;

    @Schema(description = "是否出运")
    private Integer shipmentFlag;

    @Schema(description = "转结汇单")
    private Integer settleOrderFlag;

    @Schema(description = "报关状态")
    private Integer declarationFlag;

    @Schema(description = "已转开票通知")
    private Integer inoviceNotiFlag;

    @Schema(description = "报关主体主键", example = "21247")
    private Long foreignTradeCompanyId;

    @Schema(description = "报关主体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @CompareField(value = "报关主体名称")
    private String foreignTradeCompanyName;

    @Schema(description = "船代公司主键", example = "14090")
    private Long forwarderCompanyId;

    @Schema(description = "船代公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String forwarderCompanyName;

    @Schema(description = "船次", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "船次不能为空")
    private String shipNum;

    @Schema(description = "提单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String billLadingNum;

    @Schema(description = "预计结单时间")
    private LocalDateTime estClosingTime;

    @Schema(description = "预计结关时间")
    private LocalDateTime estClearanceTime;

    @Schema(description = "预计结港时间")
    private LocalDateTime estDepartureTime;

    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fortyFootContainerNum;

    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<JsonAmount> totalGoodsValue;

    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalQuantity;

    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalBoxes;

    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonWeight totalGrossweight;

    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonWeight totalWeight;

    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalVolume;

    @Schema(description = "报关合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<JsonAmount> totalDeclaration;

    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<JsonAmount> totalPurchase;

    @Schema(description = "退税总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "28471")
    private JsonAmount totalTaxRefundPrice;

    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount commissionAmount;

    @Schema(description = "保险费用", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount insuranceFee;

    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount additionAmount;

    @Schema(description = "加项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount totalAdditionAmount;

    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount deductionAmount;

    @Schema(description = "减项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount totalDeductionAmount;

    @Schema(description = "已收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer receivedNum;

    @Schema(description = "未收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer unreceivedNum;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept inputUser;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inputDate;

    @Schema(description = "出运明细子项")
    private List<ShipmentItem> children;

    @Schema(description = "加减项列表")
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "临时产品")
    private List<TemporarySku> temporarySkuList;

    @Schema(description = "柜号")
    private String contanerNo;

    @Schema(description = "船代费用列表")
    private List<ForwarderFeeDO> forwarderFeeList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "是否自动生成")
    private Integer autoFlag;

    @Schema(description = "单证员")
    private UserDept documenter;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;

    @Schema(description = "进仓日期")
    private LocalDateTime inboundDate;

    @Schema(description = "正面唛头")
    private String frontShippingMark;

    @Schema(description = "侧面唛头")
    private String sideShippingMark;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;

    @Schema(description = "客户交期")
    private LocalDateTime custDeliveryDate;

    @Schema(description = "发票日期")
    private LocalDateTime invoiceDate;

    @Schema(description = "是否分批出运")
    private Integer batchFlag;

    @Schema(description = "来源编ID")
    private Long sourceId;
}