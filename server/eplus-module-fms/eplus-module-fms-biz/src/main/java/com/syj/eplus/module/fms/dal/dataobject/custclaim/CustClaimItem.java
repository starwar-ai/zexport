package com.syj.eplus.module.fms.dal.dataobject.custclaim;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.DifferenceReasonListHandler;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.DifferenceReason;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：客户认领明细
 * @Author：du
 * @Date：2024/7/24 11:14
 */
@TableName(value = "fms_cust_claim_item", autoResultMap = true)
@KeySequence("fms_cust_claim_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustClaimItem extends BaseDO {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 订单合同号
     */
    private String contractCode;

    /**
     * 客户编号
     */
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 订单币别
     */
    private String currency;

    /**
     * 收款方式主键
     */
    private Long settlementId;

    /**
     * 收款方式名称
     */
    private String settlementName;

    /**
     * 来源
     */
    private Integer sourceType;

    /**
     * 应收金额
     */
    private BigDecimal receivableAmount;

    /**
     * 已收金额
     */
    private BigDecimal receivedAmount;

    /**
     * 本次入账币种认领金额
     */
    private BigDecimal claimedAmount;

    /**
     * 订单币种认领金额
     */
    private BigDecimal contractAmount;

    /**
     * 差异总金额
     */
    private BigDecimal differenceAmount;

    /**
     * 收款完成标识
     */
    private Integer completedFlag;

    /**
     * 认领员工
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept claimPerson;

    /**
     * 认领日期
     */
    private LocalDateTime claimDate;

    /**
     * 差异原因
     */
    @TableField(typeHandler = DifferenceReasonListHandler.class)
    private List<DifferenceReason> differenceReason;

    /**
     * 登记主键
     */
    private Long registrationId;

    /**
     * 明细主键
     */
    private Long itemId;

    private String source;

    /**
     * 认领类型 0:回款认领 1：其他收费
     */
    private Integer type;

    /**
     * 其他收费类型  1:证书费  2:模具费  3:样品费  4:快递费 5:验货费
     */
    private Integer otherFeeType;

    /**
     * 收款单编号
     */
    private String receiptCode;

    /**
     * 财务费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount financeAmount;


    /**
     * 历史差异原因
     */
    @TableField(exist = false)
    private List<DifferenceReason> hisDifferenceReason;

    /**
     * 出运发票号
     */
    private String invoiceCode;

    /**
     * 收款比例
     */
    @TableField(exist = false)
    private BigDecimal collectionRatio;

    /**
     * 销售类型
     */
    private Integer saleType;

    /**
     * 备注
     */
    private String remark;
}