package com.syj.eplus.module.oa.dal.dataobject.paymentapp;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公对公申请 DO
 *
 * @author du
 */
@TableName(value = "oa_payment_app", autoResultMap = true)
@KeySequence("oa_payment_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAppDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 申请单号
     */
    private String code;
    /**
     * 打印状态
     * <p>
     * 枚举 {@link TODO print_flag 对应的类}
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 审核状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
    /**
     * 事由
     */
    private String reason;
    /**
     * 内部法人单位id
     */
    private Long companyId;
    /**
     * 支付对象
     */
    private Integer businessSubjectType;
    /**
     * 业务编号
     */
    private String businessSubjectCode;
    /**
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;
    /**
     * 支付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 银行地址
     */
    private String bankAddress;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行联系人
     */
    private String bankPoc;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 已支付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount paymentAmount;
    /**
     * 支付状态
     */
    private Integer paymentStatus;
    /**
     * 支付日期
     */
    private LocalDateTime paymentDate;
    /**
     * 出纳员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cashier;
    /**
     * 关联类型
     */
    private Integer relationType;
    /**
     * 关联编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> relationCode;

    /**
     * 费用归属类型
     */
    private Integer auxiliaryType;
    /**
     * 是否费用归属类型
     */
    private Integer feeShareFlag;

    /**
     * 是否预付
     */
    private Integer prepaidFlag;

    /**
     * 发票金额
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> invoiceAmountList;

    /**
     * 发票
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> invoice;

    /**
     * 预付申请单编号列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> paymentAppList;

    /**
     * 关联标识
     */
    private Integer linkFlag;

    /**
     * 科目主键
     */
    private Long financialSubjectId;

    /**
     * 类别配置主键
     */
    private Long dictSubjectId;

    /**
     * 发票金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount invoiceAmount;

    /**
     * 发票附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 累计支付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPaymentAmount;

    /**
     * 累计发票金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalInvoiceAmount;

    /**
     * 发票标识
     */
    private Integer invoiceFlag;

    /**
     * 支付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount payAmount;

    private LocalDateTime printDate;

    /**
     * 业务名称
     */
    private String businessSubjectName;

    /**
     * 备注
     */
    private String remark;
}