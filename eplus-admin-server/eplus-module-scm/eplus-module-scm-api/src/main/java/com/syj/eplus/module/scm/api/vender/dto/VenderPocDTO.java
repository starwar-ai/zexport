package com.syj.eplus.module.scm.api.vender.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class VenderPocDTO {
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
     * 名称
     */
    private String name;
    /**
     * 职位
     */
    private String pocTypes;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 地址
     */
    private String address;
    /**
     * 是否默认联系人
     */
    private Integer defaultFlag;
    /**
     * 座机
     */
    private String telephone;
    /**
     * 微信
     */
    private String wechat;
    /**
     * QQ
     */
    private String qq;

    /**
     * 备注
     */
    private String remark;

}
