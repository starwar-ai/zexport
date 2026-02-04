package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.LocalDateTimeConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleContractDetailDTO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.temporarysku.TemporarySku;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出运单 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class ShipmentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16034")
    @CompareField(value = "主键")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("出运单号")
    @CompareField(value = "编号")
    private String code;

    @Schema(description = "确认状态")
    @ExcelIgnore
    @CompareField(value = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "出运计划单号")
    private String shipmentPlanCode;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "单据状态", converter = DictConvert.class)
    @DictFormat("shipping_status")
    @CompareField(value = "单据状态")
    private Integer status;

    @Schema(description = "客户主键", example = "12793")
    @CompareField(value = "客户主键")
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "客户编号")
    private String custCode;

//    @Schema(description = "外销合同", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("外销合同")
//    @CompareField(value = "外销合同")
//    private String saleContractCode;

    @Schema(description = "客户合同", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "客户合同")
    private String custContractCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @CompareField(value = "客户名称")
    private String custName;

    @Schema(description = "发票号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发票号")
    @CompareField(value = "发票号")
    private String invoiceCode;

    @Schema(description = "价格条款", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @CompareField(value = "价格条款")
    private String settlementTermType;

    @Schema(description = "预计出运")
    @CompareField(value = "预计出运")
    private LocalDateTime estShipDate;

    @Schema(description = "出运日期")
    @ExcelProperty("预计出运日期")
    @CompareField(value = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "单据员", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "单据员")
    private UserDept orderManager;

    @Schema(description = "运输方式", example = "1")
    @ExcelProperty(value = "运输方式", converter = DictConvert.class)
    @DictFormat("transport_type")
    @CompareField(value = "运输方式")
    private Integer transportType;

    @Schema(description = "外销员", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "外销员")
    private UserDept sales;

    @Schema(description = "出运国主键", example = "6887")
    @CompareField(value = "出运国主键")
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @CompareField(value = "出运国名称")
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "出运国区域")
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", example = "29501")
    @CompareField(value = "出运口岸主键")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("出运口岸")
    @CompareField(value = "出运口岸名称")
    private String departurePortName;

    @Schema(description = "贸易国别主键", example = "2970")
    @CompareField(value = "贸易国别主键")
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @CompareField(value = "贸易国别名称")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "贸易国别区域")
    private String tradeCountryArea;

    @Schema(description = "目的口岸主键", example = "30271")
    @CompareField(value = "目的口岸主键")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("目的口岸")
    @CompareField(value = "目的口岸名称")
    private String destinationPortName;

    @Schema(description = "贸易方式", example = "1")
    @CompareField(value = "贸易方式")
    private Integer tradeType;

    @Schema(description = "是否出仓")
    @CompareField(value = "是否出仓")
    private Integer outboundFlag;

    @Schema(description = "出仓日期")
    @CompareField(value = "出仓日期")
    private LocalDateTime outboundDate;

    @Schema(description = "是否出运")
    @CompareField(value = "是否出运")
    private Integer shipmentFlag;

    @Schema(description = "转结汇单")
    @CompareField(value = "转结汇单")
    private Integer settleOrderFlag;

    @Schema(description = "报关状态")
    @CompareField(value = "报关状态")
    private Integer declarationFlag;

    @Schema(description = "已转开票通知")
    @CompareField(value = "已转开票通知")
    private Integer inoviceNotiFlag;

    @Schema(description = "报关主体主键", example = "21247")
    @CompareField(value = "报关主体主键")
    private Long foreignTradeCompanyId;

    @Schema(description = "报关主体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("报关主体名称")
    @CompareField(value = "报关主体名称")
    private String foreignTradeCompanyName;

    @Schema(description = "船代公司主键", example = "14090")
    @CompareField(value = "船代公司主键")
    private Long forwarderCompanyId;

    @Schema(description = "船代公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @CompareField(value = "船代公司名称")
    private String forwarderCompanyName;

    @Schema(description = "船次", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "船次")
    private String shipNum;

    @Schema(description = "提单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "提单号")
    private String billLadingNum;

    @Schema(description = "预计结单时间")
    @CompareField(value = "预计结单时间")
    private LocalDateTime estClosingTime;

    @Schema(description = "预计结关时间")
    @CompareField(value = "预计结关时间")
    private LocalDateTime estClearanceTime;

    @Schema(description = "预计结港时间")
    @CompareField(value = "预计结港时间")
    private LocalDateTime estDepartureTime;
    
    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "20尺柜")
    private Integer twentyFootCabinetNum;
    
    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "40尺柜")
    private Integer fortyFootCabinetNum;
    
    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "40尺高柜")
    private Integer fortyFootContainerNum;
    
    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "散货")
    private BigDecimal bulkHandlingVolume;
    
    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "货值合计")
    private List<JsonAmount> totalGoodsValue;
    
    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "数量合计")
    private Integer totalQuantity;
    
    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "箱数合计")
    private Integer totalBoxes;
    
    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "毛重合计")
    private JsonWeight totalGrossweight;
    
    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "净重合计")
    private JsonWeight totalWeight;
    
    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "体积合计")
    private BigDecimal totalVolume;
    
    @Schema(description = "报关合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "报关合计")
    private List<JsonAmount> totalDeclaration;
    
    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "采购合计")
    private List<JsonAmount> totalPurchase;
    
    @Schema(description = "退税总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "28471")
    @CompareField(value = "退税总额")
    private JsonAmount totalTaxRefundPrice;
    
    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "佣金金额")
    private JsonAmount commissionAmount;
    
    @Schema(description = "保险费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "保险费用")
    private JsonAmount insuranceFee;
    
    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "加项金额")
    private JsonAmount additionAmount;
    
    @Schema(description = "加项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "加项总额")
    private JsonAmount totalAdditionAmount;
    
    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "减项金额")
    private JsonAmount deductionAmount;
    
    @Schema(description = "减项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "减项总额")
    private JsonAmount totalDeductionAmount;

    @Schema(description = "已收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "已收货值")
    private Integer receivedNum;
    
    @Schema(description = "未收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "未收货值")
    private Integer unreceivedNum;
    
    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "录入人",converter = UserDeptConverter.class)
    @CompareField(value = "录入人")
    private UserDept inputUser;
    
    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "录入日期", converter = LocalDateTimeConvert.class)
    @CompareField(value = "录入日期")
    private LocalDateTime inputDate;
    
    @Schema(description = "创建时间")
    @CompareField(value = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "出运明细子项")
    private List<ShipmentItem> children;

    @Schema(description = "加减项列表")
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "临时产品")
    private List<TemporarySku> temporarySkuList;

    @Schema(description = "船代费用列表")
    private List<ForwarderFeeDO> forwarderFeeList;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    @Schema(description = "开票标识")
    private Integer invoiceNoticeFlag;

    @Schema(description = "柜号")
    private String contanerNo;

    @Schema(description = "关联编号")
    private List<String> linkCodeList;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "是否自动生成")
    private Integer autoFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "单证员")
    private UserDept documenter;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String saleContractCode;

    @Schema(description = "业务员")
    private String managerName;

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

    @Schema(description = "采购合同详情列表")
    private List<SimpleContractDetailDTO> contractDetails;

    @Schema(description = "是否分批出运")
    private Integer batchFlag;

    @Schema(description = "来源ID")
    private Long sourceId;
}