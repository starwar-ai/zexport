package com.syj.eplus.module.sms.dal.dataobject.otherfee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 其他费用 DO
 *
 * @author ePlus
 */

@TableName(value = "sms_other_fee", autoResultMap = true)
@KeySequence("sms_other_fee_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherFeeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 报价单id
     */
    private Long smsQuotationId;
    /**
     * 费用名称
     */
    private String feeName;
    /**
     * 金额
     */
    @Schema(description = "金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

}