package com.syj.eplus.module.oa.dal.dataobject.reimb;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import com.syj.eplus.module.oa.handler.JsonLoanAppListHandler;
import com.syj.eplus.module.oa.handler.JsonReimbDetailListHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出差报销 DO
 *
 * @author ePlus
 */
@TableName(autoResultMap = true, value = "oa_reimb")
@KeySequence("oa_reimb_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReimbDO extends BaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 表单编码
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
     */
    private Integer auditStatus;
    /**
     * 报销类型
     */
    private Integer reimbType;
    /**
     * 费用说明
     */
    private String description;
    /**
     * 报销主体
     */
    private Long companyId;
    /**
     * 报销人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept reimbUser;

    /**
     * 实际使用人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept actualUser;

    /**
     * 费用归属类型
     */
    private Integer auxiliaryType;

    /**
     * 关联合同列表
     * TODO 根据外销合同模块定义具体类型
     */
    private String contractList;

    /**
     * 发票总金额列表
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> invoiceAmountList;
    /**
     * 本次报销金额列表
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> totalAmountList;
    /**
     * 报销金额列表
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> amountList;
    /**
     * 是否还款
     */
    private Integer repayFlag;
    /**
     * 借款单还款金额列表
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> repayAmountList;
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
     * 最后支付日期
     */
    private LocalDateTime paymentDate;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行地址
     */
    private String bankAddress;
    /**
     * 开户行联系人
     */
    private String bankPoc;
    /**
     * 银行行号
     */
    private String bankCode;

    /**
     * 报销明细
     */
    @TableField(typeHandler = JsonReimbDetailListHandler.class)
    List<JsonReimbDetail> reimbDetailList;

    /**
     * 实际操作人
     */
    private Integer userFlag;

    /**
     * 出纳员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cashier;

    /**
     * 发票
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> invoice;

    /**
     * 费用用途（正式）
     */
    private String expenseUseToFormal;

    /**
     * 费用用途（发生）
     */
    private String expenseUseToOccur;

    /**
     * 科目主键
     */
    private Long financialSubjectId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 币种
     */
    private String currency;

    /**
     * 类别主键
     */
    private Long dictSubjectId;
    /**
     * 借款申请单列表
     */
    @TableField(typeHandler = JsonLoanAppListHandler.class)
    private List<SimpleLoanAppRespDTO> loanAppList;

    /**
     * 费用用途主键
     */
    private Long expenseUseToFormalId;

    /**
     * 是否草稿
     */
    private Integer draftFlag;

    /**
     * 打印日期
     */
    private LocalDateTime printDate;

    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 费用申请id列表
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> applyIdList;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 当前审批人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept approveUser;

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