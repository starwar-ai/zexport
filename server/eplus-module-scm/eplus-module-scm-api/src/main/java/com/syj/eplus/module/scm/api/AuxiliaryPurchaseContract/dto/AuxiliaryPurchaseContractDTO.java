package com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

@Data
public class AuxiliaryPurchaseContractDTO {

    //辅料采购合同信息
    private Long purchaseContractItemId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String cskuCode;
    private Integer unit;
    private SimpleFile mainPicture;
    private String contractCode;
    private Long contractId;
    private Integer quantity;
    private JsonAmount unitPrice;
    private JsonAmount withTaxPrice;
    private JsonAmount totalPrice;
    //分摊信息
    private Integer isAllocation;
    private JsonAmount allocationAmount;

    //采购合同信息
    private Long allocationPurchaseContractItemId;

}
