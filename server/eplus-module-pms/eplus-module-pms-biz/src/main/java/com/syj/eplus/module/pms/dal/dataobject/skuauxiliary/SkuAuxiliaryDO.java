package com.syj.eplus.module.pms.dal.dataobject.skuauxiliary;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

/**
 * 产品辅料配比 DO
 *
 * @author zhangcm
 */

@TableName("pms_sku_auxiliary")
@KeySequence("pms_sku_auxiliary_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuAuxiliaryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 产品名称
     */
    private String skuName;
    /**
     * 辅料编号
     */
    private String auxiliarySkuCode;
    /**
     * 辅料名称
     */
    private String auxiliarySkuName;
    /**
     * 辅料图片
     */
    @TableField(exist = false)
    private SimpleFile auxiliarySkuPicture;
    /**
     * 计量单位
     */
    @TableField(exist = false)
    private Integer measureUnit;
    /**
     * 产品比
     */
    private Integer skuRate;
    /**
     * 辅料比
     */
    private Integer auxiliarySkuRate;
    /**
     * 描述
     */
    private String description;
    /**
     * 备注
     */
    private String remark;
    /**
     * 有效标记
     */
    private Integer enableFlag;
}