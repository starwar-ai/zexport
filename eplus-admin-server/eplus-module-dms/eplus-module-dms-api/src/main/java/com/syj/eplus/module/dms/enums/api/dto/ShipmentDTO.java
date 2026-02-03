package com.syj.eplus.module.dms.enums.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipmentDTO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 确认状态
     */
    private Integer confirmFlag;
    /**
     * 单据状态
     */
    private Integer status;
    /**
     * 客户主键
     */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 外销合同
     */
    private String saleContractCode;
    /**
     * 客户合同
     */
    private String custContractCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 发票号
     */
    private String invoiceCode;
    /**
     * 价格条款
     */
    private String settlementTermType;
    /**
     * 预计出运
     */
    private LocalDateTime estShipDate;
    /**
     * 出运日期
     */
    private LocalDateTime shipDate;
    /**
     * 单据员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept orderManager;
    /**
     * 运输方式
     */
    private Integer transportType;
    /**
     * 外销员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;
    /**
     * 出运国主键
     */
    private Long departureCountryId;
    /**
     * 出运国名称
     */
    private String departureCountryName;
    /**
     * 出运国区域
     */
    private String departureCountryArea;
    /**
     * 出运口岸主键
     */
    private Long departurePortId;
    /**
     * 出运口岸名称
     */
    private String departurePortName;
    /**
     * 贸易国别主键
     */
    private Long tradeCountryId;
    /**
     * 贸易国别名称
     */
    private String tradeCountryName;
    /**
     * 贸易国别区域
     */
    private String tradeCountryArea;
    /**
     * 目的口岸主键
     */
    private Long destinationPortId;
    /**
     * 目的口岸名称
     */
    private String destinationPortName;
    /**
     * 贸易方式
     */
    private Integer tradeType;
    /**
     * 是否出仓
     */
    private Integer outboundFlag;
    /**
     * 出仓日期
     */
    private LocalDateTime outboundDate;
    /**
     * 是否出运
     */
    private Integer shipmentFlag;
    /**
     * 转结汇单
     */
    private Integer settleOrderFlag;
    /**
     * 报关状态
     */
    private Integer declarationFlag;
    /**
     * 已转开票通知
     */
    private Integer inoviceNotiFlag;
    /**
     * 内部法人单位主键
     */
    private Long companyId;
    /**
     * 内部法人单位名称
     */
    private String companyName;
    /**
     * 船代公司主键
     */
    private Long forwarderCompanyId;
    /**
     * 船代公司名称
     */
    private String forwarderCompanyName;
    /**
     * 船次
     */
    private String shipNum;
    /**
     * 提单号
     */
    private String billLadingNum;
    /**
     * 预计结单时间
     */
    private LocalDateTime estClosingTime;
    /**
     * 预计结关时间
     */
    private LocalDateTime estClearanceTime;
    /**
     * 预计结港时间
     */
    private LocalDateTime estDepartureTime;
    /**
     * 20尺柜
     */
    private Integer twentyFootCabinetNum;
    /**
     * 40尺柜
     */
    private Integer fortyFootCabinetNum;
    /**
     * 40尺高柜
     */
    private Integer fortyFootContainerNum;
    /**
     * 散货
     */
    private BigDecimal bulkHandlingVolume;
    /**
     * 货值合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalGoodsValue;
    /**
     * 数量合计
     */
    private Integer totalQuantity;
    /**
     * 箱数合计
     */
    private Integer totalBoxes;
    /**
     * 毛重合计
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;
    /**
     * 净重合计
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;
    /**
     * 体积合计
     */
    private BigDecimal totalVolume;
    /**
     * 报关合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalDeclaration;
    /**
     * 采购合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalPurchase;
    /**
     * 退税总额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalTaxRefundPrice;
    /**
     * 佣金金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;
    /**
     * 保险费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;
    /**
     * 加项金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount additionAmount;
    /**
     * 加项总额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAdditionAmount;
    /**
     * 减项金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount deductionAmount;
    /**
     * 减项总额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalDeductionAmount;
    /**
     * 已收货值
     */
    private Integer receivedNum;
    /**
     * 未收货值
     */
    private Integer unreceivedNum;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;

    /**
     * 计划编号
     */
    private String shipmentPlanCode;

    /**
     * 单证员
     */
    private UserDept documenter;


    private List<ShipmentItemDTO> children;
}
