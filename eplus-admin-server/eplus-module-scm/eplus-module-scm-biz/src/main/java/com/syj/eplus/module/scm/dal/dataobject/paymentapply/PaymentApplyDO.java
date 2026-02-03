package com.syj.eplus.module.scm.dal.dataobject.paymentapply;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.entity.ApplyPaymentPlan;
import com.syj.eplus.module.scm.entity.ApplyerPurchaseContractItem;
import com.syj.eplus.module.scm.handler.JsonApplyPaymentPlanHandler;
import com.syj.eplus.module.scm.handler.JsonApplyerPurchaseContractItemListHandler;
import com.syj.eplus.module.scm.handler.JsonPurchaseSubAddTermHandler;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 付款申请主 DO
 *
 * @author du
 */

@TableName(value = "scm_payment_apply", autoResultMap = true)
@KeySequence("scm_payment_apply_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApplyDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 付款计划id
     */
    private Long paymentPlanId;
    /**
     * 下单主体主键
     */
    private Long companyId;
    /**
     * 下单主体
     */
    private String companyName;
    /**
     * 申请人id
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;
    /**
     * 申请日期
     */
    private LocalDateTime applyDate;
    /**
     * 打印状态
     */
    private Integer printFlag;
    /**
     * 申请类型
     */
    private Integer step;
    /**
     * 申请总金额
     */
    private BigDecimal applyTotalAmount;
    /**
     * 货款总金额
     */
    private BigDecimal goodsTotalAmount;
    /**
     * 申请付款日
     */
    private LocalDateTime applyPaymentDate;
    /**
     * 申请备注
     */
    private String remark;
    /**
     * 应付供应商主键
     */
    private Long venderId;
    /**
     * 应付供应商编码
     */
    private String venderCode;
    /**
     * 应付供应商名称
     */
    private String venderName;
    /**
     * 应付币种
     */
    private String currency;
    /**
     * 付款方式id
     */
    private Long paymentId;
    /**
     * 付款方式名称
     */
    private String paymentName;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 流程实例的编号
     */
    private String processInstanceId;
    /**
     * 流程实例状态
     */
    private String paymentPlan;
    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 付款计划信息
     */
    @TableField(typeHandler = JsonApplyPaymentPlanHandler.class)
    private List<ApplyPaymentPlan> applyPaymentPlanList;

    /**
     * 采购明细
     */
    @TableField(typeHandler = JsonApplyerPurchaseContractItemListHandler.class)
    private List<ApplyerPurchaseContractItem> applyerPurchaseItemList;

    /**
     * 加减项
     */
    @TableField(value = "add_sub_term_list",typeHandler = JsonPurchaseSubAddTermHandler.class)
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

    /**
     * 采购合同编号列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> purchaseContractCodeList;

    /**
     * 付款日期
     */
    private LocalDateTime paymentDate;

    /**
     * 付款人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept paymentUser;

    /**
     * 付款状态
     */
    private Integer paymentStatus;

    /**
     * 实际付款金额
     */
    private BigDecimal realPaymentAmount;

    /**
     * 供应商银行账号
     */
    private String venderBankAccount;

    /**
     * 供应商开户行
     */
    private String venderBank;

    /**
     * 供应商开户行联系人
     */
    private String venderBankPoc;

    /**
     * 链路编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 采购员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> purchaseUserList;

    /**
     * 付款类型
     */
    private Integer paymentMarkType;

    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 承兑天数
     */
    private Integer acceptanceDays;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 加减项总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount subAddTotalAmount;

    /**
     * 作废时间
     */
    private LocalDateTime cancelTime;

    /**
     * 作废原因
     */
    private String cancelReason;

    /**
     * 作废人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cancelUser;
}