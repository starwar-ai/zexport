package com.syj.eplus.module.pms.dal.dataobject.hsdata;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 海关编码 DO
 *
 * @author ePlus
 */
@TableName("pms_hsdata")
@KeySequence("pms_hsdata_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HsdataDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 版本号
     */
    private Integer ver;

    /**
     * 编码
     */
    private String code;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 报关单位
     */
    private String unit;
    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;
    /**
     * 征税率
     */
    private BigDecimal rate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 商品全称
     */
    private String chname;
    /**
     * 征收率
     */
    private BigDecimal addrate;
    /**
     * 第二单位
     */
    private String code2;

}