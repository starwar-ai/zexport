package com.syj.eplus.module.scm.dal.dataobject.paymentapplyitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 付款申请子 DO
 *
 * @author du
 */

@TableName("scm_payment_apply_item")
@KeySequence("scm_payment_apply_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApplyItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购合同明细id
     */
    private Long purchaseContractItemId;
    /**
     * 已付金额
     */
    private BigDecimal paidAmount;
    /**
     * 已申请金额
     */
    private BigDecimal appliedAmount;
    /**
     * 本次请款金额
     */
    private Long applyAmount;
    /**
     * 开票状态
     */
    private Integer invoiceStatus;
    /**
     * 付款步骤
     */
    private Integer step;

}