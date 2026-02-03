package com.syj.eplus.module.scm.dal.dataobject.venderpoc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 供应商联系人 DO
 *
 * @author zhangcm
 */
@TableName("scm_vender_poc")
@KeySequence("scm_vender_poc_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenderPocDO extends BaseDO {

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