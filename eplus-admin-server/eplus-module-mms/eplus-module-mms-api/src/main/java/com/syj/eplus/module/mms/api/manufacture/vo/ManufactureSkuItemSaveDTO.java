package com.syj.eplus.module.mms.api.manufacture.vo;


import com.syj.eplus.framework.common.entity.JsonStockDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.util.List;

/**
 * 加工单子产品新增/修改
 */
@Data
public class ManufactureSkuItemSaveDTO {

    /**
     * 主键
     */
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
    private SimpleFile mainPicture;

    /**
     * 库存列表
     */
    private List<JsonStockDTO> stockList;

}
