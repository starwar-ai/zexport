package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
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
@Schema(description = "管理后台 - 出运计划单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ShipmentPlanRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11725")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("单据状态")
    private Integer status;
    
    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;
    
    @Schema(description = "预计出运时间")
    @ExcelProperty("预计出运时间")
    private LocalDateTime estShipDate;
    
    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("外销合同号")
    private String saleContractCode;
    
    @Schema(description = "出运国主键", example = "6709")
    @ExcelProperty("出运国主键")
    private Long departureCountryId;
    
    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("出运国名称")
    private String departureCountryName;
    
    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("出运国区域")
    private String departureCountryArea;
    
    @Schema(description = "出运口岸主键", example = "10436")
    @ExcelProperty("出运口岸主键")
    private Long departurePortId;
    
    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("出运口岸名称")
    private String departurePortName;
    
    @Schema(description = "贸易国别主键", example = "24013")
    @ExcelProperty("贸易国别主键")
    private Long tradeCountryId;
    
    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("贸易国别名称")
    private String tradeCountryName;
    
    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("贸易国别区域")
    private String tradeCountryArea;
    
    @Schema(description = "目的口岸主键", example = "10365")
    @ExcelProperty("目的口岸主键")
    private Long destinationPortId;
    
    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("目的口岸名称")
    private String destinationPortName;
    
    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("业务员")
    private UserDept sales;
    
    @Schema(description = "客户主键", example = "2935")
    @ExcelProperty("客户主键")
    private Long custId;
    
    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    private String custCode;
    
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("客户名称")
    private String custName;
    
    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;
    
    @Schema(description = "应收客户主键", example = "4838")
    @ExcelProperty("应收客户主键")
    private Long collectedCustId;
    
    @Schema(description = "应收客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("应收客户编号")
    private String collectedCustCode;
    
    @Schema(description = "应收客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("应收客户名称")
    private String collectedCustName;
    
    @Schema(description = "收货客户主键", example = "5414")
    @ExcelProperty("收货客户主键")
    private Long receiveCustId;
    
    @Schema(description = "收货客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("收货客户编号")
    private String receiveCustCode;
    
    @Schema(description = "收货客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("收货客户名称")
    private String receiveCustName;

    @Schema(description = "收款方式主键", example = "1104")
    @ExcelProperty("收款方式主键")
    private Long settlementId;
    
    @Schema(description = "收款方式名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("收款方式名称")
    private String settlementName;
    
    @Schema(description = "交易币别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("交易币别")
    private String currency;
    
    @Schema(description = "客户交期")
    @ExcelProperty("客户交期")
    private LocalDateTime custDeliveryDate;
    
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    @ExcelProperty("备注")
    private String remark;
    
    @Schema(description = "附件")
    @ExcelProperty("附件")
    private List<SimpleFile> annex;
    
    @Schema(description = "内部法人单位主键", example = "4829")
    @ExcelProperty("内部法人单位主键")
    private Long foreignTradeCompanyId;
    
    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("内部法人单位名称")
    private String foreignTradeCompanyName;
    
    @Schema(description = "20尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("20尺柜")
    private Integer twentyFootCabinetNum;
    
    @Schema(description = "40尺柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺柜")
    private Integer fortyFootCabinetNum;
    
    @Schema(description = "40尺高柜", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("40尺高柜")
    private Integer fortyFootContainerNum;
    
    @Schema(description = "散货", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("散货")
    private BigDecimal bulkHandlingVolume;
    
    @Schema(description = "货值合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("货值合计")
    private List<JsonAmount> totalGoodsValue;
    
    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("数量合计")
    private Integer totalQuantity;
    
    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("箱数合计")
    private Integer totalBoxes;
    
    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("毛重合计")
    private JsonWeight totalGrossweight;
    
    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("净重合计")
    private JsonWeight totalWeight;
    
    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("体积合计")
    private BigDecimal totalVolume;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "出运计划明细")
    private List<ShipmentPlanItem> children;

    @Schema(description = "运输方式")
    private Integer transportType;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "加减项")
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "业务员")
    private String managerName;

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