package com.syj.eplus.module.sms.api.dto;

import com.syj.eplus.framework.common.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleContractDTO {


    private  List<SaleContractItemDTO>  children;

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
     * 内部法人单位s
     */
    private Long companyId;

    /**
     * 内部法人单位名称
     */
    private String companyName;

    /**
     * 订单路径
     */
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
    private String custName;

    /**
     * 内部客户主键
     */
    private Long internalCustId;

    /**
     * 内部客户编号
     */
    private String internalCustCode;

    /**
     * 内部客户名称
     */
    private String internalCustName;

    /**
     * 交易币别
     */
    private String currency;
    /**
     * 价格条款
     */
    private String settlementTermType;

    /**
     * 收款方式主键
     */
    private Long settlementId;

    /**
     * 收款方式名称
     */
    private String settlementName;

    /**
     * 客户国别主键
     */
    private Long custCountryId;

    /**
     * 客户国别
     */
    private String custCountryName;
    /**
     * 客户合同号
     */
    private String custPo;
    /**
     * 是否代理
     */
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
    private String receiveCustName;
    /**
     * 销售人员
     */
    private UserDept sales;
    /**
     * 附件
     */
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
    private String tradeCountryName;

    /**
     * 贸易国区域
     */
    private String tradeCountryArea;

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
     * 目的口岸主键
     */
    private Long destinationPortId;

    /**
     * 目的口岸名称
     */
    private String destinationPortName;
    /**
     * 运输方式
     */
    private Integer transportType;
    /**
     * 客户交期
     */
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
    private JsonAmount trailerFee;
    /**
     * 预估总运费
     */
    private JsonAmount estimatedTotalFreight;
    /**
     * 是否订舱
     * <p>
     * 枚举
     */
    private Integer bookingFlag;
    /**
     * 佣金
     */
    private JsonAmount commission;
    /**
     * 平台费
     */
    private JsonAmount platformFee;
    /**
     * 保险费
     */
    private JsonAmount insuranceFee;
    /**
     * 保险费
     */
    private JsonAmount lumpSumFee;
    /**
     * 中信保费用
     */
    private JsonAmount sinosureFee;
    /**
     * 加项金额
     */
    private JsonAmount additionAmount;
    /**
     * 减项金额
     */
    private JsonAmount deductionAmount;
    /**
     * 验货费用
     */
    private JsonAmount inspectionFee;
    /**
     * 预计包材合计
     */
    private JsonAmount estimatedPackingMaterials;
    /**
     * 配件采购合计
     */
    private JsonAmount accessoriesPurchaseTotal;
    /**
     * 箱数合计
     */
    private Integer totalBoxes;
    /**
     * 毛重合计
     */
    private JsonWeight totalGrossweight;
    /**
     * 净重合计
     */
    private JsonWeight totalWeight;
    /**
     * 体积合计
     */
    private BigDecimal totalVolume;
    /**
     * 货值合计
     */
    private JsonAmount totalGoodsValue;
    /**
     * 采购合计
     */
    private List<JsonAmount> totalPurchase;
    /**
     * 退税合计
     */
    private JsonAmount totalVatRefund;
    /**
     * 数量合计
     */
    private Integer totalQuantity;
    /**
     * 订单毛利
     */
    private JsonAmount orderGrossProfit;
    /**
     * 毛利率
     */
    private BigDecimal grossProfitMargin;
    /**
     * 应收汇款
     */
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
     * 变更状态
     */
    private Integer changeStatus;

    /**
     * 自动生成标识 0-否 1-是
     */
    private Integer autoFlag;

    /**
     * 订单链路编号
     */
    private List<String> linkCodeList;

    /**
     * 设计稿
     */
    private List<SimpleFile> designDraftList;

    /**
     * 回签附件
     */
    private List<SimpleFile> signBackAnnex;
    /**
     * 回签标记
     */
    private Integer signBackFlag;

    /**
     * 回签描述
     */
    private String signBackDesc;
    /**
     * 客户收款账号id
     */
    private Long collectionAccountId;
    /**
     * 客户收款账号
     */
    private String collectionAccountBankCode;
    /**
     * 创建时汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 销售合同主键
     */
    private Long sourceContractId;

    /**
     * 销售合同编码
     */
    private String sourceContractCode;

    /**
     * 送货地址
     */
    private String deliveryAddress;


}
