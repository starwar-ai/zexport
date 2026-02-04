package com.syj.eplus.module.crm.dal.dataobject.custsettlement;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import lombok.*;

/**
 * 客户结汇方式 DO
 *
 * @author eplus
 */
@TableName("crm_cust_settlement")
@KeySequence("crm_cust_settlement_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustSettlementDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 结汇方式编号
     */
    private Long settlementId;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 是否缺省
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer defaultFlag = 0;

}