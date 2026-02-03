package com.syj.eplus.module.fms.controller.admin.bankregistration.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.PayeeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/18 16:13
 */
@Data
@Accessors(chain = false)
@Schema(description = "认领信息")
public class SimpleRegistrationResp {
    private Long id;

    private String code;
    /**
     * 公司抬头
     */
    private String companyTitle;

    /**
     * 银行入账日期
     */
    private LocalDateTime bankPostingDate;

    /**
     * 入账单位id
     */
    private Long companyId;
    /**
     * 入账单位名称
     */
    private String companyName;

    /**
     * 银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 入账币别
     */
    private String currency;
    /**
     * 入账金额
     */
    private BigDecimal amount;
    /**
     * 已认领金额
     */
    private BigDecimal claimedAmount;

    /**
     * 登记人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept registeredBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收款对象信息
     */
    private List<PayeeEntity> payeeEntityList;

    /**
     * 认领明细
     */
    private List<CustClaimItem> custClaimItemList;

    /**
     * 业务员列表
     */
    private List<UserDept> managerList;

    /**
     * 待认领金额
     */
    private BigDecimal unclaimedAmount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 客户列表
     */
    private List<SimpleData> custList;

    /**
     * 认领状态
     */
    private Integer claimStatus;
}
