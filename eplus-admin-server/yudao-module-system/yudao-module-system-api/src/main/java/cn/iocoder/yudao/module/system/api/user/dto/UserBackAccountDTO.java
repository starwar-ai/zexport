package cn.iocoder.yudao.module.system.api.user.dto;

import lombok.Data;

@Data
public class UserBackAccountDTO {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 银行
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
}
