package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购合同 修改 设计文件 Request VO")
@Data
public class PurchaseContractDesignSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    private Long id;

    @Schema(description = "出片文件")
    private List<SimpleFile> designDraftList;
}
