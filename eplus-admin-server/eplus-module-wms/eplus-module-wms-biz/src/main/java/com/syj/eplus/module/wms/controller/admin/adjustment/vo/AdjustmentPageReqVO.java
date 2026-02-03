package com.syj.eplus.module.wms.controller.admin.adjustment.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-盘库调整单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdjustmentPageReqVO extends PageParam {

    @Schema(description = "调整单单号")
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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] adjustmentDate;

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
