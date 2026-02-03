package com.syj.eplus.module.infra.api.company.dto;

import lombok.Data;

/**
 * 公司银行信息 Response DTO
 *
 * @author du
 */
@Data
public class CompanyBankRespDTO  {

    /**
     * 主键
     */
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
     * 默认标记
     */
    private Integer defaultFlag;

    /**
      * 是否默认
      */
    private Integer companyNature;

    /**
     *启用标识
     */
    private Integer enableFlag;

    /**
     *简称
     */
    private String shortname;

}
