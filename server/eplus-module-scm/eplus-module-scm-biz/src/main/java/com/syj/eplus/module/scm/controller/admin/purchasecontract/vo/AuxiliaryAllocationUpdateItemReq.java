package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 辅料列表 req VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class AuxiliaryAllocationUpdateItemReq {


    @Schema(description = "分摊金额")
    private JsonAmount allocationAmount;

    @Schema(description = "采购合同明细id")
    private Long contractItemId;


}
