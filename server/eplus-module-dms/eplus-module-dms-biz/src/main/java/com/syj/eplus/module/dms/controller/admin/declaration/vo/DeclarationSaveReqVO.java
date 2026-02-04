package com.syj.eplus.module.dms.controller.admin.declaration.vo;

import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 报关单新增/修改 Request VO")
@Data
public class DeclarationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16417")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "发票号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String invoiceCode;

    @Schema(description = "制单日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inputDate;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "报关单位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String declarationUnit;

    @Schema(description = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "外销币种", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "贸易国别主键", example = "4513")
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tradeCountryArea;

    @Schema(description = "贸易方式", example = "2")
    private Integer tradeType;

    @Schema(description = "价格条款", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String settlementTermType;

    @Schema(description = "结汇方式", example = "26577")
    private Long settlementId;

    @Schema(description = "结汇名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String settlementName;

    @Schema(description = "船代公司主键", example = "24762")
    private Long forwarderCompanyId;

    @Schema(description = "船代公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String forwarderCompanyName;

    @Schema(description = "集装箱数量")
    private Integer containerQuantity;

    @Schema(description = "运输方式", example = "1")
    private Integer transportType;

    @Schema(description = "出运口岸主键", example = "16484")
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String departurePortName;

    @Schema(description = "目的口岸", example = "19461")
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", example = "王五")
    private String destinationPortName;

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

    @Schema(description = "外贸公司主体主键", example = "22523")
    private Long foreignTradeCompanyId;

    @Schema(description = "外贸公司主体名称")
    private String foreignTradeCompanyName;

    @Schema(description = "计划单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shipmentPlanCode;

    @Schema(description = "报关单子项")
    private List<DeclarationItem> children;

    @Schema(description = "20尺柜")
    private Integer twentyFootCabinetNum;

    @Schema(description = "40尺柜")
    private Integer fortyFootCabinetNum;

    @Schema(description = "40尺高柜")
    private Integer fortyFootContainerNum;

    @Schema(description = "散货")
    private BigDecimal bulkHandlingVolume;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "出运明细编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shipmentCode;

    @Schema(description = "出运国主键")
    private Long departureCountryId;

    @Schema(description = "出运国名称")
    private String departureCountryName;

    @Schema(description = "出运国区域")
    private String departureCountryArea;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;

    @Schema(description = "正面唛头")
    private String frontShippingMark;

    @Schema(description = "侧面唛头")
    private String sideShippingMark;

    @Schema(description = "收货人")
    private String receivePerson;

    @Schema(description = "通知人")
    private String notifyPerson;

    @Schema(description = "报关协议号")
    private String protocolCode;

    @Schema(description = "预计结港时间")
    private LocalDateTime estDepartureTime;

    @Schema(description = "发票日期")
    private LocalDateTime invoiceDate;
}