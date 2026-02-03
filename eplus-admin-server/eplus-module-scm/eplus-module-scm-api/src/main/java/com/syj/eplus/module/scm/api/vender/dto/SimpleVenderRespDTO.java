package com.syj.eplus.module.scm.api.vender.dto;

import com.syj.eplus.framework.common.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimpleVenderRespDTO {
    /**
     * 供应商id
     */
    private Long id;
    /**
     * 供应商编码
     */
    private String code;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 供应商简称
     */
    private String nameShort;

    /**
     * 供应商简称
     */
    private String address;
    /**
     * 供应商类型
     */
    private String venderType;

    /**
     * 供应商英文名称
     */
    private String nameEng;
    /**
     * 是否境外供应商
     */
    private Integer abroadFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 银行信息列表
     */
    List<BankAccount> bankAccountList;

    /**
     * 采购员
     */
    private List<Long> buyerIds;
    /**
     * 采购员
     */
    private List<BaseValue> buyerList;
    /**
     * 采购员列表
     */
    private List<UserDept> buyers;

    /**
     * 应付供应商
     */
    private List<SimpleData> venderLink;

    /**
     * 内部企业标识 0-否 1-是
     */
    private Integer internalFlag;

    /**
     * 内部企业主键
     */
    private Long internalCompanyId;

    /**
     * 付款方式列表
     */
    private List<VenderPaymentDTO> paymentList;

    /**
     * 快递所在地区id
     */
    private Integer deliveryAreaId;
    /**
     * 快递所在地区id列表
     */
    private List<Integer> deliveryAreaIdList;
    /**
     * 快递所在地址名字
     */
    private String deliveryAreaName;
    /**
     * 快递地址
     */
    private String deliveryAddress;
    /**
     * 供应商联系人列表
     */
    private List<VenderPocDTO> pocList;

    private List<JsonVenderTax> taxMsg;

    /**
     * 币种
     */
    private String currency;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 发票类型
     */
    private Integer taxType;
}
