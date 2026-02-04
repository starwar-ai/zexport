package com.syj.eplus.module.home.dal.dataobject.invoiceholder;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发票夹 DO
 *
 * @author du
 */

@TableName(value = "home_invoice_holder", autoResultMap = true)
@KeySequence("home_invoice_holder_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHolderDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 报销类型
     */
    private Integer reimbType;
    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 报销金额
     */
    private BigDecimal reimbAmount;
    /**
     * 报销事项
     */
    private String reimbItem;
    /**
     * 报销凭证
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> invoice;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;

    /**
     * 费用说明
     */
    private String description;

    /**
     * 发票号
     */
    private String invoiceCode;

    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 类别主键
     */
    private Long dictSubjectId;

    /**
     * 发票日期
     */
    private LocalDateTime invoiceDate;
}