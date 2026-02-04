package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SimplePurchaseContractItemDTO {

    /**
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 产品skuId
     */
    private Long skuId;

    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;

    /**
     * 采购币种
     */
    private String purchaseCurrency;

    /**
     * 采购数量
     */
    private Integer quantity;

    /**
     * 包装价
     */
    private JsonAmount packagingPrice;

    /**
     * 运费
     */
    private JsonAmount shippingPrice;

    /**
     * 含税单价
     */
    private JsonAmount withTaxPrice;

    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;

    /**
     * 采购员id
     */
    private UserDept purchaseUser;

    /**
     * 供应商名称
     */
    private String venderName;

    /**
     * 产品编号
     */
    private String skuCode;

}
