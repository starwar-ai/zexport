package com.syj.eplus.module.crm.controller.admin.custsettlement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 客户结汇方式新增/修改 Request VO")
@Data
public class CustSettlementSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "结汇方式编号", example = "1873")
    private Long settlementId;

    @Schema(description = "是否缺省")
    private Integer defaultFlag;

}