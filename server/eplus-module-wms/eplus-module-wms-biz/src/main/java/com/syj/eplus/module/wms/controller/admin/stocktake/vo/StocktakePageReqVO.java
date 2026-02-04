package com.syj.eplus.module.wms.controller.admin.stocktake.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-盘点单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StocktakePageReqVO extends PageParam {

    @Schema(description = "归属公司主键", example = "54")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    private String companyName;

    @Schema(description = "仓库主键", example = "31460")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "张三")
    private String warehouseName;

    @Schema(description = "盘点单号")
    private String code;

    @Schema(description = "盘库状态  1-未开始、2-盘库中、3-已结束", example = "2")
    private Integer stocktakeStatus;

    @Schema(description = "实际盘点开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] actStartTime;

    @Schema(description = "实际盘点结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] actEndTime;

    @Schema(description = "采购合同主键", example = "17341")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "22083")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "盘点人主键", example = "1814")
    private Long stocktakeUserId;

    @Schema(description = "盘点人姓名", example = "赵六")
    private String stocktakeUserName;

    @Schema(description = "id数组")
    private List<Long> idList;

    @Schema(description = "产品编号", example = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

}
