package com.syj.eplus.module.pms.api.sku.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

@Data
public class SkuAuxiliaryDTO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 产品id
     */
    private Long skuId;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 产品名称
     */
    private String skuName;

    /**
     * 辅料id
     */
    private Long auxiliarySkuId;
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
    private SimpleFile auxiliarySkuPicture;
    /**
     * 计量单位
     */
    private Integer auxiliarySkuUnit;
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
}
