package com.syj.eplus.module.pms.dal.dataobject.skubom;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 产品SKU BOM DO
 *
 * @author eplus
 */
@TableName("pms_sku_bom")
@KeySequence("pms_sku_bom_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuBomDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 子SKU编号
     */
    private Long skuId;
    /**
     * SKU版本
     */
    private Integer skuVer;
    /**
     * sku code
     */
    private String skuCode;
    /**
     * 数量
     */
    private Integer qty;
    /**
     * 父SKU编号
     */
    private Long parentSkuId;

    /**
     * 产品类型
     */
    private Integer skuType;

}