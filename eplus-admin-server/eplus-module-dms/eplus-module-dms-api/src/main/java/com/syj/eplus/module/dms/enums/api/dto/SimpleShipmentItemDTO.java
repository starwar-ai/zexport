package com.syj.eplus.module.dms.enums.api.dto;

import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import lombok.Data;

import java.util.List;

@Data
public class SimpleShipmentItemDTO {
    /**
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 产品skuId
     */
    private Long skuId;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

}
