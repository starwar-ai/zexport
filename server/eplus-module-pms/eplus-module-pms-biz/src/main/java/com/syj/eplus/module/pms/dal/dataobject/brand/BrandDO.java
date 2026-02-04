package com.syj.eplus.module.pms.dal.dataobject.brand;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

/**
 * 品牌 DO
 *
 * @author ePlus
 */
@TableName(value = "pms_brand",autoResultMap = true)
@KeySequence("pms_brand_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;
    /**
     * 流水号长度
     */
    private String nameEng;
    /**
     * 商品说明
     */
    private String description;
    /**
     * 商品说明英文
     */
    private String descriptionEng;
    /**
     * 是否自主品牌
     */
    private Integer ownBrandFlag;
    /**
     * 品牌类型
     */
    private Integer brandType;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 品牌logo
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile logo;
}