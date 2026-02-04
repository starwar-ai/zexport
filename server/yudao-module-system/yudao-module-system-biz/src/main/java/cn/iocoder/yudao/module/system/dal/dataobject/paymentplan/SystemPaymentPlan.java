package cn.iocoder.yudao.module.system.dal.dataobject.paymentplan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 付款计划 DO
 *
 * @author du
 */

@TableName("system_payment_plan")
@KeySequence("system_payment_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPaymentPlan extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 收款方式主键
     */
    private Long paymentId;
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    /**
     * 步骤
     */
    private Integer step;
    /**
     * 起始点
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer days;
    /**
     * 付款比例
     */
    private BigDecimal paymentRatio;
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

}