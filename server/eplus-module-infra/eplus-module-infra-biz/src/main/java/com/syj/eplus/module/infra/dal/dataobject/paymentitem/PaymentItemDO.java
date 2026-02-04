package com.syj.eplus.module.infra.dal.dataobject.paymentitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import lombok.*;

/**
 * 付款方式 DO
 *
 * @author ePlus
 */
@TableName("system_payment_item")
@KeySequence("system_payment_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemDO extends BaseDO {

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
     * 名称
     */
    private String name;
    /**
     * 审核状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
    /**
     * 起始日类型
     * <p>
     * 枚举 {@link TODO date_type 对应的类}
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer duration;
    /**
     * 英文名称
     */
    private String nameEng;

}