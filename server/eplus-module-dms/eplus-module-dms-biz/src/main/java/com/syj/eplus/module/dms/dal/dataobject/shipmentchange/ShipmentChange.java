package com.syj.eplus.module.dms.dal.dataobject.shipmentchange;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentRespVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.temporarysku.TemporarySku;
import com.syj.eplus.module.dms.handler.*;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/29/20:21
 * @Description:
 */
@TableName(value = "dms_shipment_change", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class ShipmentChange extends BaseDO implements ModelKeyHolder {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16034")
    private Long id;

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
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept orderManager;

    @Schema(description = "运输方式", example = "1")
    private Integer transportType;

    @Schema(description = "外销员", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
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

    @Schema(description = "内部法人单位主键", example = "21247")
    private Long companyId;

    @Schema(description = "内部法人单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String companyName;

    @Schema(description = "船代公司主键", example = "14090")
    private Long forwarderCompanyId;

    @Schema(description = "船代公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String forwarderCompanyName;

    @Schema(description = "船次", requiredMode = Schema.RequiredMode.REQUIRED)
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
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalGoodsValue;

    @Schema(description = "数量合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalQuantity;

    @Schema(description = "箱数合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalBoxes;

    @Schema(description = "毛重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;

    @Schema(description = "净重合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;

    @Schema(description = "体积合计", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalVolume;

    @Schema(description = "报关合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalDeclaration;

    @Schema(description = "采购合计", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalPurchase;

    @Schema(description = "退税总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "28471")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalTaxRefundPrice;

    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;

    @Schema(description = "保险费用", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    @Schema(description = "加项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount additionAmount;

    @Schema(description = "加项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAdditionAmount;

    @Schema(description = "减项金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount deductionAmount;

    @Schema(description = "减项总额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalDeductionAmount;

    @Schema(description = "已收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer receivedNum;

    @Schema(description = "未收货值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer unreceivedNum;

    @Schema(description = "出运明细子项")
    @TableField(typeHandler = JsonShipmentItemHandler.class)
    private List<ShipmentItem> children;

    @Schema(description = "加减项列表")
    @TableField(typeHandler = JsonAddSubItemDTOHandler.class)
    private List<AddSubItemDTO> addSubItemList;

    @Schema(description = "临时产品")
    @TableField(typeHandler = JsonTemporarySkuHandler.class)
    private List<TemporarySku> temporarySkuList;

    @Schema(description = "船代费用")
    @TableField(typeHandler = JsonForwarderFeeListHandler.class)
    private List<ForwarderFeeDO> forwarderFeeList;

    @Schema(description = "出运单编号")
    private String shipmentCode;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "录入人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;

    private String processInstanceId;

    private Integer auditStatus;

    private String modelKey;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "计划编号")
    private String shipmentPlanCode;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("单据状态")
    @CompareField(value = "单据状态")
    private Integer status;

    @TableField(typeHandler = JsonShipmentRespHandler.class)
    private ShipmentRespVO oldData;

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
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> purchaseUserList;

    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> managerList;

    /**
     * 进仓日期
     */
    private LocalDateTime inboundDate;

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
     * 客户交期
     */
    private LocalDateTime custDeliveryDate;

    /**
     * 单证员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept documenter;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;
}
