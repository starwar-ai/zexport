package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ReLockItem {

    @Schema(description = "销售合同明细id")
    private Long saleContractItemId;

    @Schema(description = "库存明细")
    private List<StockLockSaveReqVO> lockSaveReqVOList;

}
