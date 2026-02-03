package com.syj.eplus.module.mms.api.manufacture.vo;


import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.util.List;

/**
 * 加工单产品新增/修改
 */
@Data
public class ManufactureSkuSaveDTO {


    /**
     * 主键
     */
    private Long id;

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
     * 产品图片
     */
    private SimpleFile mainPicture;

    /**
     * 销售合同主键
     */
    private Long smsContractId;

    /**
     * 销售合同编号
     */
    private String smsContractCode;

    /**
     * 子产品列表
     */
    private List<ManufactureSkuItemSaveDTO> skuItemList;

}
