package com.syj.eplus.module.dal.dataobject.sendproduct;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

/**
 * 寄件产品 DO
 *
 * @author zhangcm
 */

@TableName(value = "ems_send_product",autoResultMap = true)
@KeySequence("ems_send_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendProductDO extends BaseDO {

    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 寄件id
     */
    private Long sendId;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 物件来源
     */
    @CompareField(value = "物件来源")
    private Integer goodsSource;
    /**
     * 产品编号
     */
    @CompareField(value = "产品编号")
    private String skuCode;
    /**
     * 产品名称
     */
    @CompareField(value = "产品名称")
    private String skuName;
    /**
     * 图片
     */
    @CompareField(value = "图片")
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile picture;
    /**
     * 数量
     */
    @CompareField(value = "数量")
    private Integer quantity;

}