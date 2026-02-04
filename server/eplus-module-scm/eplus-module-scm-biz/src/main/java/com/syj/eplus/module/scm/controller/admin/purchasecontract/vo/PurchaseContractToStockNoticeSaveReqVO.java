package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemToStockNoticeRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购合同新增/修改 Request VO")
@Data
public class PurchaseContractToStockNoticeSaveReqVO {

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "到货时间")
    private LocalDateTime expectDate;

    @Schema(description = "采购员id")
    private UserDept purchaseUserId;

    @Schema(description = "备注")
    private  String remark;

    @Schema(description = "产品明细列表")
    private List<PurchaseContractItemToStockNoticeRespVO>  children;

}