package com.syj.eplus.module.pms.dal.dataobject.spu;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 商品 DO
 *
 * @author eplus
 */
@TableName("pms_spu")
@KeySequence("pms_spu_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuDO extends BaseDO {

    /**
     * 主键
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
     * 产品条码
     */
    private String barcode;
    /**
     * 英文名称
     */
    private String nameEng;
    /**
     * 品牌编号
     */
    private Long brandId;
    /**
     * 商品分类
     */
    private Long categoryId;
    /**
     * 计量单位
     */
    private String unitType;
    /**
     * 商品状态
     */
    private Integer onshelfFlag;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 商品说明
     */
    private String description;
    /**
     * 商品说明英文
     */
    private String descriptionEng;
    /**
     * 海关编码
     */
    private Long hsCodeId;
    /**
     * 是否自主品牌
     */
    private Integer ownBrandFlag;

    /**
     * 产品规格
     */
    private String spec;

    /**
     * 海关编码版本
     */
    private Integer hsCode_var;
}