package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SkuSimpleVO {


    private Long skuId;


    private String skuCode;


    private String skuName;


}