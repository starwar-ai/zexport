package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 20:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePurchaseContract {
    private PurchaseContractInfoRespVO old_purchaseContractInfoRespVO;
    private PurchaseContractInfoRespVO purchaseContractInfoRespVO;
    @Schema(description = "包材标记")
    private Integer auxiliaryFlag;
    @Schema(description = "提交审核标识")
    private Integer submitFlag;
}
