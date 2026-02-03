package com.syj.eplus.module.crm.dal.dataobject.custbankaccount;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 银行账户信息 DO
 *
 * @author 芋道源码
 */
@TableName("crm_cust_bankaccount")
@KeySequence("crm_cust_bankaccount_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustBankaccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 版本号
     */
    private Integer ver;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 开户行地址
     */
    private String bankAddress;
    /**
     * 开户行联系人
     */
    private String bankPoc;
    /**
     * 银行账号
     */
    private String bankCode;
    /**
     * 银行账户
     */
    private String bankAccount;
    /**
     * 是否默认账户0-否，1-是
     */
    private Integer defaultFlag;

    /**
     * 收款方id
     */
    private Long payeeEntityId;

}