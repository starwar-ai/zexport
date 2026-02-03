package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外销合同 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SaleContractSimpleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13247")
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "确认状态")
    @ExcelProperty("确认状态")
    @ExcelIgnore
    private Integer confirmFlag;

    @Schema(description = "内部法人单位主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    @CompareField
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "12365")
    @ExcelProperty(value = "内部法人单位")
    @CompareField
    private String companyName;

    @Schema(description = "订单路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("订单路径")
    @CompareField
    private String orderPath;

    @Schema(description = "客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long custId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "客户")
    @CompareField
    private String custName;

    @Schema(description = "交易币别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("交易币别")
    @CompareField
    private String currency;

    @Schema(description = "价格条款", example = "1")
    @ExcelProperty("价格条款")
    @CompareField
    private String settlementTermType;

    @Schema(description = "收款方式主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long settlementId;

    @Schema(description = "收款方式名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "收款方式")
    @CompareField
    private String settlementName;

    @Schema(description = "客户国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long custCountryId;

    @Schema(description = "客户国别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "客户国别")
    @CompareField
    private String custCountryName;

    @Schema(description = "客户区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String custAreaName;

    @Schema(description = "客户合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户合同号")
    @CompareField
    private String custContractCode;

    @Schema(description = "是否代理", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "是否代理", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    @CompareField
    private Integer agentFlag;

    @Schema(description = "应收客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long collectedCustId;

    @Schema(description = "应收客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String collectedCustCode;

    @Schema(description = "应收客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "应收客户")
    @CompareField
    private String collectedCustName;

    @Schema(description = "收货客户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long receiveCustId;

    @Schema(description = "收货客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String receiveCustCode;

    @Schema(description = "收货客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "收货客户")
    @CompareField
    private String receiveCustName;

    @Schema(description = "销售人员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "销售人员", converter = UserDeptConverter.class)
    @CompareField
    private UserDept sales;


    @Schema(description = "录入日期")
    @ExcelProperty("录入日期")
    @CompareField
    private LocalDateTime inputDate;

    @Schema(description = "贸易国别主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long tradeCountryId;

    @Schema(description = "贸易国别名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "贸易国别")
    @CompareField
    private String tradeCountryName;

    @Schema(description = "贸易国别区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String tradeCountryArea;

    @Schema(description = "出运国主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long departureCountryId;

    @Schema(description = "出运国名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "出运国")
    @CompareField
    private String departureCountryName;

    @Schema(description = "出运国区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private String departureCountryArea;

    @Schema(description = "出运口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long departurePortId;

    @Schema(description = "出运口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "出运口岸")
    @CompareField
    private String departurePortName;

    @Schema(description = "目的口岸主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField
    private Long destinationPortId;

    @Schema(description = "目的口岸名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "目的口岸")
    @CompareField
    private String destinationPortName;

    @Schema(description = "运输方式", example = "1")
    @ExcelProperty("运输方式")
    @CompareField
    private Integer transportType;

    @Schema(description = "客户交期")
    @ExcelProperty("客户交期")
    @CompareField
    private LocalDateTime custDeliveryDate;


    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("数量合计")
    @CompareField
    private Integer totalQuantity;


    @Schema(description = "销售合同类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("销售合同类型")
    @CompareField
    private Integer saleType;


    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


    @Schema(description = "实收金额")
    @CompareField
    private JsonAmount receivedAmount;

    @Schema(description = "总计金额")
    @CompareField
    private JsonAmount totalGoodsValue;


    @Schema(description = "单据状态")
    private Integer auditStatus;


    @Schema(description = "销售明细")
    private List<SaleContractItem> children;


    @Schema(description = "创建时汇率")
    private BigDecimal exchangeRate;

}