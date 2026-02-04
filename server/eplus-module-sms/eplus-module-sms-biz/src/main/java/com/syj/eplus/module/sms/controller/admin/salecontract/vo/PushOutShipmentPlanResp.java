package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/12 10:19
 */
@Data
public class PushOutShipmentPlanResp {
    @Schema(description = "预计出运时间")
    private LocalDateTime estShipDate;

    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String saleContractCode;

    @Schema(description = "出运国主键", example = "6709")
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", example = "10436")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String departurePortName;

    @Schema(description = "贸易国别主键", example = "24013")
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryArea;

    @Schema(description = "目的口岸主键", example = "10365")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String destinationPortName;

    @Schema(description = "客户主键", example = "2935")
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "应收客户主键", example = "4838")
    private Long collectedCustId;

    @Schema(description = "应收客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collectedCustCode;

    @Schema(description = "应收客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String collectedCustName;

    @Schema(description = "收货客户主键", example = "5414")
    private Long receiveCustId;

    @Schema(description = "收货客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiveCustCode;

    @Schema(description = "收货客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String receiveCustName;

    @Schema(description = "收款方式主键", example = "1104")
    private Long settlementId;

    @Schema(description = "收款方式名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String settlementName;

    @Schema(description = "交易币别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "客户交期")
    private LocalDateTime custDeliveryDate;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    private String remark;

    @Schema(description = "附件")
    private String annex;

    @Schema(description = "外贸公司主键", example = "4829")
    private Long foreignTradeCompanyId;

    @Schema(description = "外贸公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String foreignTradeCompanyName;
    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fortyFootContainerNum;

    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private String totalGoodsValue;

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

    @Schema(description = "出运计划明细", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PushOytShipmentPlanItem> children;

    @Schema(description = "运输方式")
    private Integer transportType;

    @Schema(description = "加减项")
    private List<AddSubItem> addSubItemList;

    @Schema(description = "出货客户")
    private List<SimpleCustSaveResp> shipmentCustList;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;

    @Schema(description = "业务员名称")
    private String managerName;
    /**
     * 价格条款
     */
    @Schema(description = "价格条款")
    private String settlementTermType;

    @Schema(description = "预计交货日期")
    private LocalDateTime estDeliveryDate;

    @Schema(description = "正面唛头")
    private String frontShippingMark;

    @Schema(description = "侧面唛头")
    private String sideShippingMark;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;
}
