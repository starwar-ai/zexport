package cn.iocoder.yudao.module.system.dal.dataobject.collectionplan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/15 17:24
 */

@TableName("system_collection_plan")
@KeySequence("system_collection_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCollectionPlan extends BaseDO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 收款计划主键
     */
    private Long settlementId;

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
     * 收款比例
     */
    private BigDecimal collectionRatio;


    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;

    /**
     * 是否控制出运
     */
    private Integer controlShipmentFlag;

    /**
     * 状态
     */
    private Integer exeStatus;

    /**
     * 支付方式
     */
    private Integer paymentMethod;
}
