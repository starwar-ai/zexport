package com.syj.eplus.module.sms.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SmsContractAllDTO {
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
     * 内部法人单位s
     */
    private Long companyId;

    /**
     * 内部法人单位名称
     */
    @CompareField(value = "下单主体")
    private String companyName;

    /**
     * 订单路径
     */
    @CompareField(value = "订单路径")
    @TableField(typeHandler = JsonCompanyPathTypeHandler.class)
    private JsonCompanyPath companyPath;

    /**
     * 客户主键
     */
    private Long custId;

    /**
     * 客户编号
     */
    private String custCode;

    /**
     * 客户名称
     */
    @CompareField(value = "客户名称")
    private String custName;
    /**
     * 交易币别
     */
    @CompareField(value = "交易币别")
    private String currency;
    /**
     * 价格条款
     */
    @CompareField(value = "价格条款")
    private String settlementTermType;

    /**
     * 收款方式主键
     */
    private Long settlementId;

    /**
     * 收款方式名称
     */
    @CompareField(value = "收款方式")
    private String settlementName;

    /**
     * 客户国别主键
     */
    private Long custCountryId;

    /**
     * 客户国别
     */
    @CompareField(value = "客户国别")
    private String custCountryName;
    /**
     * 客户合同号
     */
    @CompareField(value = "客户合同号")
    private String custPo;
    /**
     * 是否代理
     */
    @CompareField(value = "是否代理", enumType = "is_int")
    private Integer agentFlag;

    /**
     * 应收客户主键
     */
    private Long collectedCustId;

    /**
     * 应收客户编号
     */
    private String collectedCustCode;

    /**
     * 应收客户名称
     */
    @CompareField(value = "应收客户")
    private String collectedCustName;

    /**
     * 收货客户主键
     */
    private Long receiveCustId;

    /**
     * 收货客户编号
     */
    private String receiveCustCode;

    /**
     * 收货客户名称
     */
    @CompareField(value = "收货客户")
    private String receiveCustName;
    /**
     * 销售人员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "销售人员")
    private UserDept sales;
    /**
     * 附件
     */
    @CompareField(value = "附件")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 录入日期
     */
    private LocalDateTime inputDate;

    /**
     * 贸易国别主键
     */
    private Long tradeCountryId;

    /**
     * 贸易国别名称
     */
    @CompareField(value = "贸易国别")
    private String tradeCountryName;

    /**
     * 贸易国区域
     */
    @CompareField(value = "贸易国区域")
    private String tradeCountryArea;

    /**
     * 出运国主键
     */
    private Long departureCountryId;

    /**
     * 出运国名称
     */
    @CompareField(value = "出运国")
    private String departureCountryName;

    /**
     * 出运国区域
     */
    @CompareField(value = "出运国区域")
    private String departureCountryArea;

    /**
     * 出运口岸主键
     */
    private Long departurePortId;

    /**
     * 出运口岸名称
     */
    @CompareField(value = "出运口岸")
    private String departurePortName;

    /**
     * 目的口岸主键
     */
    private Long destinationPortId;

    /**
     * 目的口岸名称
     */
    @CompareField(value = "目的口岸")
    private String destinationPortName;
    /**
     * 运输方式
     */
    @CompareField(value = "运输方式", enumType = "transport_type")
    private Integer transportType;
    /**
     * 客户交期
     */
    @CompareField(value = "客户交期")
    private LocalDateTime custDeliveryDate;
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
     * 拖柜费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount trailerFee;
    /**
     * 预估总运费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount estimatedTotalFreight;
    /**
     * 是否订舱
     * <p>
     * 枚举 {@link TODO is_int 对应的类}
     */
    private Integer bookingFlag;
    /**
     * 佣金
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commission;
    /**
     * 平台费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount platformFee;
    /**
     * 保险费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;
    /**
     * 保险费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount lumpSumFee;
    /**
     * 中信保费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount sinosureFee;
    /**
     * 加项金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount additionAmount;
    /**
     * 减项金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount deductionAmount;
    /**
     * 验货费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount inspectionFee;
    /**
     * 预计包材合计
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount estimatedPackingMaterials;
    /**
     * 配件采购合计
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount accessoriesPurchaseTotal;
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
     * 货值合计
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalGoodsValue;
    /**
     * 采购合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalPurchase;
    /**
     * 退税合计
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalVatRefund;
    /**
     * 数量合计
     */
    private Integer totalQuantity;
    /**
     * 订单毛利
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount orderGrossProfit;
    /**
     * 毛利率
     */
    private BigDecimal grossProfitMargin;
    /**
     * 应收汇款
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivableExchange;
    /**
     * 销售合同类型
     */
    private Integer saleType;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 转采购计划标记
     */
    private Integer convertPurchaseFlag;

    /**
     * 转采购计划时间
     */
    private LocalDateTime convertPurchaseTime;

    /**
     * 流程id
     */
    private String processInstanceId;

    /**
     * 回签人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept signBackUser;

    /**
     * 回签日期
     */
    private LocalDateTime signBackDate;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */

    private  LocalDateTime createTime;
    /**
     * 费用归属金额
     */
    private JsonAmount amount;
}
