package com.syj.eplus.module.infra.dal.dataobject.company;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 公司银行信息 DO
 *
 * @author eplus
 */
@TableName("system_company_bank")
@KeySequence("system_company_bank_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBank extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 公司主键
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司英文名称
     */
    private String companyNameEng;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行英文名称
     */
    private String bankNameEng;
    /**
     * 银行地址
     */
    private String bankAddress;
    /**
     * 银行英文地址
     */
    private String bankAddressEng;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 银行识别代码
     */
    private String swiftCode;
    /**
     * 是否默认
     */
    private Integer defaultFlag;
}