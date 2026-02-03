package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ReLockReq{

    @Schema(description = "销售合同主键")
    private String saleContractCode;

    @Schema(description = "锁定信息")
    private List<ReLockItem> itemList;
}
