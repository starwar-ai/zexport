package com.syj.eplus.module.wms.controller.admin.transferorder.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.dal.dataobject.transferorderitem.TransferOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 调拨新增/修改 Request VO")
@Data
public class TransferOrderSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6691")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "库存主体主键", example = "29248")
    private Long companyId;

    @Schema(description = "库存主体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String companyName;

    @Schema(description = "调拨类型", example = "1")
    private Integer transferType;

    @Schema(description = "拨入订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String saleContractCode;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String custName;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept inputUser;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "库存明细")
    private List<TransferOrderItem> children;

    @Schema(description = "提交标识")
    private Integer submitFlag;
}