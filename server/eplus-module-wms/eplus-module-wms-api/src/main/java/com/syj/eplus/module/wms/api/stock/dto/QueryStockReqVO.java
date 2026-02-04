package com.syj.eplus.module.wms.api.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 查询产品批次库存 Request VO")
@Data
public class QueryStockReqVO {

    @NotNull(message = "产品编码不能为空")
    @Schema(description = "产品编码", example = "1024")
    private String skuCode;

    @NotNull(message = "归属公司主键不能为空")
    @Schema(description = "归属公司主键", example = "1024")
    private List<Long> companyIdList;

    @Schema(description = "原单据明细主键", example = "1024")
    private  Long sourceOrderItemId;

    @Schema(description = "原单据类型", example = "1024")
    private  Integer sourceOrderType;

    @Schema(description = "外销合同编码", example = "1024")
    private  String saleContractCode;

    @Schema(description = "外销合同明细Id", example = "1024")
    private  Long saleContractItemId;

    @Schema(description = "是否只查询锁定库存", example = "1024")
    private Integer onlyLockFlag;

    @Schema(description = "是否包含加工主体")
    private Integer producedFlag;

    @Schema(description = "是否只查可用数量")
    private Integer onlyAvailableQuantityFlag;
}
