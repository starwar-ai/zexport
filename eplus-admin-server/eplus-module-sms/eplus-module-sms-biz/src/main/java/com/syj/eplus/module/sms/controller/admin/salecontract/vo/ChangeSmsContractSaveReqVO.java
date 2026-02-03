package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description：销售合同变更请求实体
 * @Author：du
 * @Date：2024/7/1 14:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSmsContractSaveReqVO {

    private SaleContractRespVO old_saleContractRespVO;

    private SaleContractRespVO saleContractRespVO;


    @Schema(description = "提交审核标识")
    private Integer submitFlag;
}
