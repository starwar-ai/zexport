package com.syj.eplus.module.scm.api.purchasecontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
public class CompleteOrderReqDTO {

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime doneTime;

    @Schema(description = "合同ID")
    private Long contractId;

    @Schema(description = "合同完成数量")
    private Map<String,Integer> usedQuantityMap;

    @Schema(description = "内销标识")
    private Boolean domesticSaleFlag;

    @Schema(description = "批次号")
    private List<String> batchCodeList;

    @Schema(description = "是否校验状态")
    private Boolean checkStatus;
}
