package com.syj.eplus.module.crm.api.cust.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/18 15:34
 */
@Data
public class CustBankAccountDTO {

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
     * 客户名称
     */
    private String custName;
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
}
