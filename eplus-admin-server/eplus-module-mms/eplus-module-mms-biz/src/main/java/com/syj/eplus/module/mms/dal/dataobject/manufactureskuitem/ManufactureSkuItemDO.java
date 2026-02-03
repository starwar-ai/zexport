package com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonStockDTOListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonStockDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.util.List;

/**
 * 加工单子产品 DO
 *
 * @author zhangcm
 */

@TableName(value = "mms_manufacture_sku_item",autoResultMap = true)
@KeySequence("mms_manufacture_sku_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufactureSkuItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 加工单产品id
     */
    private Long manufactureSkuId;
    /**
     * 加工单id
     */
    private Long manufactureId;
    /**
     * 产品id
     */
    private Long skuId;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 客户产品编号
     */
    private String cskuCode;
    /**
     * 产品名称
     */
    private String skuName;
    /**
     * 产品数量
     */
    private Integer quantity;
    /**
     * 配比
     */
    private Integer ratio;
    /**
     * 产品图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;

    /**
     * 库存列表
     */
    @TableField(typeHandler = JsonStockDTOListTypeHandler.class)
    private List<JsonStockDTO> stockList;

}