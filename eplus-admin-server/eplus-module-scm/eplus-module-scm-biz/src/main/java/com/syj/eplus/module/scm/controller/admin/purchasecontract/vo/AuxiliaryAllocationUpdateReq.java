package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 辅料列表 req VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class AuxiliaryAllocationUpdateReq {


    @Schema(description = "辅料合同明细id")
    private Long auxiliaryContractItemId;

    @Schema(description = "分摊列表")
    private List<AuxiliaryAllocationUpdateItemReq> itemList;


}
