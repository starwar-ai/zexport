package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/10 17:36
 */
@Schema(description = "管理后台 - 历史成交价 Response VO")
@Data
public class HistoryTradePriceResp {

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "中文品名")
    private String skuName;

    @Schema(description = "成交价")
    private JsonAmount tradePrice;

    @Schema(description = "采购价")
    private JsonAmount purchasePrice;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "销售合同")
    private String saleContractCode;
//
//    @Schema(description = "采购合同")
//    private String purchaseContractCode;

//    @Schema(description = "采购时间")
//    private LocalDateTime purchaseTime;

    @Schema(description = "销售时间")
    private LocalDateTime saleTime;

    @Schema(description = "客户国别")
    private String custCountryName;

    @Schema(description = "销售数量")
    private Integer quantity;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 规格
     */
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
    private List<JsonSpecificationEntity> specificationList;
}
