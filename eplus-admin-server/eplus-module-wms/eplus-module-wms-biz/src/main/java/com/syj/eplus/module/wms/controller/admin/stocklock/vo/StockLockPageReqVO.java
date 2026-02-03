package com.syj.eplus.module.wms.controller.admin.stocklock.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-库存明细-锁定库存分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockLockPageReqVO extends PageParam {

    @Schema(description = "产品库存主键", example = "8212")
    @NotNull(message = "产品库存主键不能为空")
    private Long stockId;

    @Schema(description = "销售合同单号")
    private String saleContractCode;

    @Schema(description = "原单据类型  1-销售合同 2-加工单 3-调拨单 4-采购计划", example = "2")
    private Integer sourceOrderType;

    @Schema(description = "锁定类型  1-可用数量 2-未到货库存", example = "1")
    private Integer lockType;

    @Schema(description = "锁定数量")
    private Integer lockQuantity;

    @Schema(description = "锁定时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lockTime;

    @Schema(description = "锁定人主键", example = "9891")
    private Long lockByUserId;

    @Schema(description = "锁定人名称", example = "王五")
    private String lockByUserName;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
