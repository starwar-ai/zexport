package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出运计划单新增/修改 Request VO")
@Data
public class ShipmentPlanSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11725")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inputDate;

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

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept sales;

    @Schema(description = "客户PO号")
    private String custPo;

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
    private List<SimpleFile> annex;

    @Schema(description = "内部法人单位主键", example = "4829")
    private Long foreignTradeCompanyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
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

    @Schema(description = "出运计划明细", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShipmentPlanItem> children;

    @Schema(description = "运输方式")
    private Integer transportType;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "加减项")
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;

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

    private Integer submitFlag;
}
