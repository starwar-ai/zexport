package com.syj.eplus.module.wms.controller.admin.adjustment.vo;

import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-盘库调整单新增/修改 Request VO")
@Data
public class AdjustmentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21313")
    private Long id;

    @Schema(description = "调整单单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "调整单单号不能为空")
    private String code;

    @Schema(description = "盘点单主键", example = "28160")
    private Long stocktakeId;

    @Schema(description = "盘点单单号", example = "28160")
    private String stocktakeCode;

    @Schema(description = "盘点人主键", example = "28160")
    private Long stocktakeUserId;

    @Schema(description = "盘点人姓名", example = "28160")
    private String stocktakeUserName;

    @Schema(description = "调整类型  1-盘盈单 2-盘亏单", example = "1")
    private Integer adjustmentType;

    @Schema(description = "采购合同主键", example = "12549")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "15329")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "24225")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    private String warehouseName;

    @Schema(description = "调整日期")
    private LocalDateTime adjustmentDate;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "20666")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "李四")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "盘库调整单-明细")
    private List<AdjustmentItemSaveReqVO> adjustmentItemList;
}
