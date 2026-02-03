package com.syj.eplus.module.dms.controller.admin.shipment.vo.change;

import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentRespVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.temporarysku.TemporarySku;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/30/18:36
 * @Description:
 */
@Data
public class ChangeShipmentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16034")
    @ExcelProperty("主键")
    @CompareField(value = "主键")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    @CompareField(value = "编号")
    private String code;

    @Schema(description = "确认状态")
    @ExcelProperty("确认状态")
    @ExcelIgnore
    @CompareField(value = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "计划编号")
    private String shipmentPlanCode;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("单据状态")
    @CompareField(value = "单据状态")
    private Integer status;

    @Schema(description = "客户主键", example = "12793")
    @ExcelProperty("客户主键")
    @CompareField(value = "客户主键")
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    @CompareField(value = "客户编号")
    private String custCode;

    @Schema(description = "外销合同", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("外销合同")
    @CompareField(value = "外销合同")
    private String saleContractCode;

    @Schema(description = "客户合同", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户合同")
    @CompareField(value = "客户合同")
    private String custContractCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("客户名称")
    @CompareField(value = "客户名称")
    private String custName;

    @Schema(description = "发票号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发票号")
    @CompareField(value = "发票号")
    private String invoiceCode;

    @Schema(description = "价格条款", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("价格条款")
    @CompareField(value = "价格条款")
    private String settlementTermType;

    @Schema(description = "预计出运")
    @ExcelProperty("预计出运")
    @CompareField(value = "预计出运")
    private LocalDateTime estShipDate;

    @Schema(description = "出运日期")
    @ExcelProperty("出运日期")
    @CompareField(value = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "单据员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据员")
    @CompareField(value = "单据员")
    private UserDept orderManager;

    @Schema(description = "运输方式", example = "1")
    @ExcelProperty("运输方式")
    @CompareField(value = "运输方式")
    private Integer transportType;

    @Schema(description = "外销员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("外销员")
    @CompareField(value = "外销员")
    private UserDept sales;

    @Schema(description = "出运国主键", example = "6887")
    @ExcelProperty("出运国主键")
    @CompareField(value = "出运国主键")
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("出运国名称")
    @CompareField(value = "出运国名称")
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("出运国区域")
    @CompareField(value = "出运国区域")
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", example = "29501")
    @ExcelProperty("出运口岸主键")
    @CompareField(value = "出运口岸主键")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("出运口岸名称")
    @CompareField(value = "出运口岸名称")
    private String departurePortName;

    @Schema(description = "贸易国别主键", example = "2970")
    @ExcelProperty("贸易国别主键")
    @CompareField(value = "贸易国别主键")
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("贸易国别名称")
    @CompareField(value = "贸易国别名称")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("贸易国别区域")
    @CompareField(value = "贸易国别区域")
    private String tradeCountryArea;

    @Schema(description = "目的口岸主键", example = "30271")
    @ExcelProperty("目的口岸主键")
    @CompareField(value = "目的口岸主键")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("目的口岸名称")
    @CompareField(value = "目的口岸名称")
    private String destinationPortName;

    @Schema(description = "贸易方式", example = "1")
    @ExcelProperty("贸易方式")
    @CompareField(value = "贸易方式")
    private Integer tradeType;

    @Schema(description = "是否出仓")
    @ExcelProperty("是否出仓")
    @CompareField(value = "是否出仓")
    private Integer outboundFlag;

    @Schema(description = "出仓日期")
    @ExcelProperty("出仓日期")
    @CompareField(value = "出仓日期")
    private LocalDateTime outboundDate;

    @Schema(description = "是否出运")
    @ExcelProperty("是否出运")
    @CompareField(value = "是否出运")
    private Integer shipmentFlag;

    @Schema(description = "转结汇单")
    @ExcelProperty("转结汇单")
    @CompareField(value = "转结汇单")
    private Integer settleOrderFlag;

    @Schema(description = "报关状态")
    @ExcelProperty("报关状态")
    @CompareField(value = "报关状态")
    private Integer declarationFlag;

    @Schema(description = "已转开票通知")
    @ExcelProperty("已转开票通知")
    @CompareField(value = "已转开票通知")
    private Integer inoviceNotiFlag;

    @Schema(description = "内部法人单位主键", example = "21247")
    @ExcelProperty("内部法人单位主键")
    @CompareField(value = "内部法人单位主键")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("内部法人单位名称")
    @CompareField(value = "内部法人单位名称")
    private String companyName;

    @Schema(description = "船代公司主键", example = "14090")
    @ExcelProperty("船代公司主键")
    @CompareField(value = "船代公司主键")
    private Long forwarderCompanyId;

    @Schema(description = "船代公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("船代公司名称")
    @CompareField(value = "船代公司名称")
    private String forwarderCompanyName;

    @Schema(description = "船次", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("船次")
    @CompareField(value = "船次")
    private String shipNum;

    @Schema(description = "提单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提单号")
    @CompareField(value = "提单号")
    private String billLadingNum;

    @Schema(description = "预计结单时间")
    @ExcelProperty("预计结单时间")
    @CompareField(value = "预计结单时间")
    private LocalDateTime estClosingTime;

    @Schema(description = "预计结关时间")
    @ExcelProperty("预计结关时间")
    @CompareField(value = "预计结关时间")
    private LocalDateTime estClearanceTime;

    @Schema(description = "预计结港时间")
    @ExcelProperty("预计结港时间")
    @CompareField(value = "预计结港时间")
    private LocalDateTime estDepartureTime;

    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("20尺柜")
    @CompareField(value = "20尺柜")
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺柜")
    @CompareField(value = "40尺柜")
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺高柜")
    @CompareField(value = "40尺高柜")
    private Integer fortyFootContainerNum;

    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("散货")
    @CompareField(value = "散货")
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("货值合计")
    @CompareField(value = "货值合计")
    private List<JsonAmount> totalGoodsValue;

    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("数量合计")
    @CompareField(value = "数量合计")
    private Integer totalQuantity;

    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("箱数合计")
    @CompareField(value = "箱数合计")
    private Integer totalBoxes;

    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("毛重合计")
    @CompareField(value = "毛重合计")
    private JsonWeight totalGrossweight;

    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("净重合计")
    @CompareField(value = "净重合计")
    private JsonWeight totalWeight;

    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("体积合计")
    @CompareField(value = "体积合计")
    private BigDecimal totalVolume;

    @Schema(description = "报关合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("报关合计")
    @CompareField(value = "报关合计")
    private List<JsonAmount> totalDeclaration;

    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value ="采购合计", converter = AmountConvert.class)
    @CompareField(value = "采购合计")
    private List<JsonAmount> totalPurchase;

    @Schema(description = "退税总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "28471")
    @ExcelProperty(value = "退税总额", converter = AmountConvert.class)
    @CompareField(value = "退税总额")
    private JsonAmount totalTaxRefundPrice;

    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value= "佣金金额", converter = AmountConvert.class)
    @CompareField(value = "佣金金额")
    private JsonAmount commissionAmount;

    @Schema(description = "保险费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value="保险费用", converter = AmountConvert.class)
    @CompareField(value = "保险费用")
    private JsonAmount insuranceFee;

    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value="加项金额", converter = AmountConvert.class)
    @CompareField(value = "加项金额")
    private JsonAmount additionAmount;

    @Schema(description = "加项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value="加项总额", converter = AmountConvert.class)
    @CompareField(value = "加项总额")
    private JsonAmount totalAdditionAmount;

    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value="减项金额", converter = AmountConvert.class)
    @CompareField(value = "减项金额")
    private JsonAmount deductionAmount;

    @Schema(description = "减项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value="减项总额", converter = AmountConvert.class)
    @CompareField(value = "减项总额")
    private JsonAmount totalDeductionAmount;

    @Schema(description = "已收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("已收货值")
    @CompareField(value = "已收货值")
    private Integer receivedNum;

    @Schema(description = "未收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("未收货值")
    @CompareField(value = "未收货值")
    private Integer unreceivedNum;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "录入人")
    private UserDept inputUser;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "录入日期")
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @CompareField(value = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "出运明细子项")
    private List<ShipmentItem> children;

    @Schema(description = "加减项列表")
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "临时产品")
    private List<TemporarySku> temporarySkuList;

    @Schema(description = "变更状态")
    private Integer changeStatus;

    private ShipmentRespVO oldData;

    private String processInstanceId;

    private Integer submitFlag;

    private Integer auditStatus;

    @Schema(description = "船代费用列表")
    private List<ForwarderFeeDO> forwarderFeeList;

    /**
     * 正面唛头
     */
    private String frontShippingMark;

    /**
     * 侧面唛头
     */
    private String sideShippingMark;

    /**
     * 收货人
     */
    private String receivePerson;

    /**
     * 通知人
     */
    private String notifyPerson;


    /**
     * 柜号
     */
    private String contanerNo;


    /**
     * 备注
     */
    private String remark;


    /**
     * 采购员
     */
    private List<UserDept> purchaseUserList;

    /**
     * 业务员
     */
    private List<UserDept> managerList;

    /**
     * 进仓日期
     */
    private LocalDateTime inboundDate;

    /**
     * 客户交期
     */
    private LocalDateTime custDeliveryDate;
}
