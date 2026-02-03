package com.syj.eplus.module.scm.dal.dataobject.purchaseregistration;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购跟单登记 DO
 *
 * @author du
 */

@TableName(value = "scm_purchase_registration", autoResultMap = true)
@KeySequence("scm_purchase_registration_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRegistrationDO extends BaseDO {

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
     * 付款主体主键
     */
    private Long companyId;
    /**
     * 付款主题名称
     */
    private String companyName;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 发票类型
     * <p>
     * 枚举 {@link TODO tax_type 对应的类}
     */
    private Integer taxType;
    /**
     * 税票编号
     */
    private String invoiceCode;
    /**
     * 收票日期
     */
    private LocalDateTime invoicedDate;
    /**
     * 发票总金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 币别
     */
    private String currency;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    //状态
    private Integer status;

    /**
     * 录入人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept inputUser;

    /**
     * 录入日期
     */
    private LocalDateTime inputDate;

    /**
     * 流程实例编号
     */
    private String processInstanceId;

    /**
     * 复核日期
     */
    private LocalDateTime reviewDate;


    /**
     * 复核人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept reviewUser;


}