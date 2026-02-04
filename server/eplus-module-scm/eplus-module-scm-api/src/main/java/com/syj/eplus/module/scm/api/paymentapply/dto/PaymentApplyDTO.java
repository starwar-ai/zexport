package com.syj.eplus.module.scm.api.paymentapply.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 付款申请主 DO
 *
 * @author du
 */


@Data
public class PaymentApplyDTO extends BaseDO {

    /**
     * 主键
     */
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
     * 采购明细
     */
    private List<ApplyerPurchaseContractItemDTO> applyerPurchaseItemList;


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
}