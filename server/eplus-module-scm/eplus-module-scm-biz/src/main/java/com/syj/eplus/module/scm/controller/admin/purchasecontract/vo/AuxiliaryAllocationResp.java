package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 辅料列表 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class AuxiliaryAllocationResp {



    @Schema(description = "产品编号")
    public Long skuId;

    @Schema(description = "产品编号")
    public String skuCode;

    @Schema(description = "产品名称")
    public String skuName;

    @Schema(description = "客户货号")
    public String cskuCode;






}
