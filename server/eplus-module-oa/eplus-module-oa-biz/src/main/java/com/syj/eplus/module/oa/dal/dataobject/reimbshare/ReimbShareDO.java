package com.syj.eplus.module.oa.dal.dataobject.reimbshare;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import lombok.*;

import java.math.BigDecimal;

/**
 * 报销分摊 DO
 *
 * @author ePlus
 */

@TableName("oa_reimb_share")
@KeySequence("oa_reimb_share_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReimbShareDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(value = "`id`")
    private Long reimbShareId;
    /**
     * 报销单编号
     */
    private Long reimbId;
    /**
     * 费用归属类型
     *
     * 枚举 {@link TODO auxiliary_type 对应的类}
     */
    private Integer auxiliaryType;
    /**
     * 费用归属对象id
     */
    private Long auxiliaryId;
    /**
     * 分摊比例
     */
    private BigDecimal ratio;

}