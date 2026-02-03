package com.syj.eplus.module.controller.admin.send.vo.feeshare;

import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class FeeSharePurchaseRespVO   {

    @Schema(description = "客户信息")
    private PurchaseContractAllDTO purchaseContract;

}