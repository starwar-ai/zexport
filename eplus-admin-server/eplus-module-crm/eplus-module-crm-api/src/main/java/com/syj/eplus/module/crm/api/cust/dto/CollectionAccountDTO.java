package com.syj.eplus.module.crm.api.cust.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CollectionAccountDTO extends BaseDO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 默认标记
     */
    private Integer defaultFlag;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 内部公司主键
     */
    private Long companyId;
    /**
     * 内部公司银行账号主键
     */
    private Long companyBankId;
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

}