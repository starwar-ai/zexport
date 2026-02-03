package com.syj.eplus.module.wms.controller.admin.stocktake.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-盘点单新增/修改 Request VO")
@Data
public class StocktakeSaveReqVO {

    @Schema(description = "归属公司主键", example = "54")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    private String companyName;

    @Schema(description = "仓库主键", example = "31460")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "张三")
    private String warehouseName;

    @Schema(description = "采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "预计盘点日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern=FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND,timezone = "GMT+8")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime planDate;

    @Schema(description = "盘点人主键", example = "1814")
    private Long stocktakeUserId;

    @Schema(description = "盘点人姓名", example = "赵六")
    private String stocktakeUserName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "盘点单-明细列表")
    private List<StocktakeItemSaveReqVO> itemSaveReqVOList;


    @Schema(description = "主键", example = "20841")
    private Long id;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

}
