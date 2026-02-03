package com.syj.eplus.module.scm.dal.dataobject.venderbankaccount;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 供应商银行账户 DO
 *
 * @author zhangcm
 */
@TableName("scm_vender_bankaccount")
@KeySequence("scm_vender_bankaccount_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenderBankaccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 版本
     */
    private Integer ver;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商版本
     */
    private Integer venderVer;
    /**
     * 银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 是否默认账号
     */
    private Integer defaultFlag;

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

}