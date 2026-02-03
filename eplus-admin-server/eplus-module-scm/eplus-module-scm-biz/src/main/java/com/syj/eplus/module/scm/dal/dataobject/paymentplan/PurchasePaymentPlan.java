package com.syj.eplus.module.scm.dal.dataobject.paymentplan;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonPaymentListHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonPayment;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购合同付款计划 DO
 *
 * @author du
 */

@TableName(value = "scm_payment_plan", autoResultMap = true)
@KeySequence("scm_payment_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class PurchasePaymentPlan extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 步骤
     */
    @CompareField(value = "步骤",enumType = "payment_plan_step")
    private Integer step;
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    /**
     * 起始点
     */
    @CompareField(value = "起始点",enumType = "date_type")
    private Integer dateType;
    /**
     * 起始日
     */
    private LocalDateTime startDate;
    /**
     * 天数
     */
    @CompareField(value = "天数")
    private Integer days;
    /**
     * 预计付款日
     */
    private LocalDateTime expectedReceiptDate;
    /**
     * 付款比例
     */
    private BigDecimal paymentRatio;
    /**
     * 应付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivableAmount;
    /**
     * 实付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivedAmount;

    /**
     * 已申请金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount appliedAmount;
    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;

    /**
     * 是否控制发票
     */
    private Integer controlInvoiceFlag;
    /**
     * 状态
     */
    private Integer exeStatus;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    /**
     * 本次申请金额
     */
    @TableField(exist = false)
    private BigDecimal applyAmount;

    /**
     * 实际付款比例
     */
    private BigDecimal realPaymentRatio;

    /**
     * 付款时间
     */
    private LocalDateTime paymentTime;

    /**
     * 付款信息
     */
    @TableField(typeHandler = JsonPaymentListHandler.class)
    private List<JsonPayment> paymentMsg;

    @TableField(exist = false)
    private List<PurchasePaymentItem> itemList;

    /**
     * 付款方式名称
     */
    private String paymentName;

    /**
     * 水单
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
}