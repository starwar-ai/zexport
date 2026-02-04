package com.syj.eplus.module.wms.controller.admin.transferorder.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.dal.dataobject.transferorderitem.TransferOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 调拨 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TransferOrderRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6691")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "库存主体主键", example = "29248")
    @ExcelProperty("库存主体主键")
    private Long companyId;

    @Schema(description = "库存主体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("库存主体名称")
    private String companyName;

    @Schema(description = "调拨类型", example = "1")
    @ExcelProperty("调拨类型")
    private Integer transferType;

    @Schema(description = "拨入订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("拨入订单号")
    private String saleContractCode;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入人")
    private UserDept inputUser;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "调拨明细")
    List<TransferOrderItem> transferOrderItemList;
}