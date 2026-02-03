package com.syj.eplus.module.pms.api.sku.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.util.List;

/**
 * 产品SKU BOM DO
 *
 * @author eplus
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuBomDTO extends BaseDO {

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

    /**
     * 相关自营产品编号列表
     */
    private List<String> ownBrandSkuCodeList;

}
