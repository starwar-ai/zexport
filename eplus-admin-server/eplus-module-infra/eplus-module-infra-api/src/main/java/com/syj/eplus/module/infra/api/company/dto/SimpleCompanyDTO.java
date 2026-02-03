package com.syj.eplus.module.infra.api.company.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimpleCompanyDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 公司性质
     */
    private Integer companyNature;

    /**
     * 公司抬头
     */
    private String companyName;

    /**
     * 公司可用币种
     */
    private List<String> availableCurrencyList;

    /**
     * 税号
     */
    private String taxNumb;

    /**
     * 海关编号
     */
    private String customsNumber;

    /**
     * 图片
     */
    private String picture;

    /**
     * 简称
     */
    private String shortname;

    /**
     * 企业英文名称
     */
    private String companyNameEng;

    /**
     * 企业地址
     */
    private String companyAddress;

    /**
     * 企业地址英文
     */
    private String companyAddressEng;


    /**
     * 企业电话
     */
    private String companyTel;

    /**
     * 企业传真
     */
    private String companyFax;

    /**
     * 公司银行信息
     */
    private CompanyBankRespDTO companyBankRespDTO;
}
