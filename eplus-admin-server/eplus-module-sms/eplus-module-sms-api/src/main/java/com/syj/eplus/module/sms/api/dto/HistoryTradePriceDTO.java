package com.syj.eplus.module.sms.api.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryTradePriceDTO {


    private String cskuCode;


    private String custName;

    private String skuName;

    private JsonAmount tradePrice;

    private JsonAmount purchasePrice;

    private Integer purchaseQuantity;

    private String saleContractCode;

    private LocalDateTime saleTime;
}
