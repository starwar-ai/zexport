package com.syj.eplus.module.controller.admin.send.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SendCompareRespVO extends SendDO {

     @Schema(description = "导入金额")
     private JsonAmount inputCost;

//    @Schema(description = "确认金额")
//    private BigDecimal sureCost;
}