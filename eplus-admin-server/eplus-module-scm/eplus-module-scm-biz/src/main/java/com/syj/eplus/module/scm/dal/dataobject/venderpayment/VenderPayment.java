package com.syj.eplus.module.scm.dal.dataobject.venderpayment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import lombok.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/06/11:45
 * @Description: 供应商付款方式实体类
 */
@TableName("scm_vender_payment")
@KeySequence("scm_vender_payment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenderPayment extends BaseDO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 付款方式编号
     */
    private Long paymentId;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 是否缺省
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer defaultFlag = 0;

}
