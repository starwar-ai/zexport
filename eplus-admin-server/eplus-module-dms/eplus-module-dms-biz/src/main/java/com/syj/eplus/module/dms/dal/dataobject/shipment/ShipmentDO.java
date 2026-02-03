package com.syj.eplus.module.dms.dal.dataobject.shipment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出运单 DO
 *
 * @author du
 */

@TableName(value ="dms_shipment", autoResultMap = true)
@KeySequence("dms_shipment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDO extends BaseDO {

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
//    /**
//     * 外销合同
//     */
//    private String saleContractCode;
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
     * 外贸公司主体主键
     */
    private Long foreignTradeCompanyId;
    /**
     * 外贸公司主体名称
     */
    private String foreignTradeCompanyName;
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
     * 变更状态
     */
    private Integer changeStatus;

    /**
     * 开票标识
     */
    private Integer invoiceNoticeFlag;

    /**
     * 柜号
     */
    private String contanerNo;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 来源编号
     */
    private String sourceCode;

    /**
     * 是否自动生成
     */
    private Integer autoFlag;


    /**
     * 备注
     */
    private String remark;

    /**
     * 单证员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept documenter;

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
     * 内部生成编号
     */
    private String genContractUniqueCode;

    /**
     * 客户交期
     */
    private LocalDateTime custDeliveryDate;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 是否分批出运
     */
    private Integer batchFlag;

    /**
     * 来源明细id
     */
    private Long sourceId;
}