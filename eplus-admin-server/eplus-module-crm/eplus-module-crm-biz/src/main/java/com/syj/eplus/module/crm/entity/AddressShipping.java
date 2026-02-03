package com.syj.eplus.module.crm.entity;

import lombok.Data;

@Data
public class AddressShipping {

    /**
     * 收件人名称
     */
    private String name;
    /**
     * 收件地址
     */
    private String address;
    /**
     * 邮编
     */
    private String postalCode;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 是否默认
     */
    private Integer defaultFlag;

    /**
     * 地址类型
     */
    private Integer addressType;

    /**
     * 大货地址
     */
    private String bulkAddress;

    /**
     * 快递地址
     */
    private String deliveryAddress;

    /**
     * 备注
     */
    private String remarks;
}
