package com.syj.eplus.module.dms.dal.dataobject.settlementform;

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
 * 结汇单 DO
 *
 * @author du
 */

@TableName(value = "dms_settlement_form", autoResultMap = true)
@KeySequence("dms_settlement_form_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementFormDO extends BaseDO {

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
     * 外贸公司主体主键
     */
    private Long foreignTradeCompanyId;
    /**
     * 外贸公司主体名称
     */
    private String foreignTradeCompanyName;
    /**
     * 发票号
     */
    private String invoiceCode;
    /**
     * 制单日期
     */
    private LocalDateTime inputDate;
    /**
     * 出运日期
     */
    private LocalDateTime shipDate;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 外销币种
     */
    private String currency;
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
     * 出运国别主键
     */
    private Long departureCountryId;
    /**
     * 出运国别名称
     */
    private String departureCountryName;
    /**
     * 出运国别区域
     */
    private String departureCountryArea;
    /**
     * 贸易方式
     */
    private Integer tradeType;
    /**
     * 价格条款
     */
    private String settlementTermType;
    /**
     * 结汇方式
     */
    private Long settlementId;
    /**
     * 结汇名称
     */
    private String settlementName;
    /**
     * 船代公司主键
     */
    private Long forwarderCompanyId;
    /**
     * 船代公司名称
     */
    private String forwarderCompanyName;
    /**
     * 集装箱数量
     */
    private Integer containerQuantity;
    /**
     * 运输方式
     */
    private Integer transportType;
    /**
     * 发票箱单
     */
    private String invoicePackingList;
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
     * 出运单号
     */
    private String shipmentCode;
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
     * 金额合计
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;

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
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 货值合计
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalGoodsValue;

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
     * 预计结港时间
     */
    private LocalDateTime estDepartureTime;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;
}