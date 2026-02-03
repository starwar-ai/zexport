package com.syj.eplus.module.wms.controller.admin.stocktake.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-盘点单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StocktakeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20841")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "盘点单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("盘点单号")
    private String code;

    @Schema(description = "审核状态", example = "2")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "盘库状态  1-未开始、2-盘库中、3-已结束", example = "2")
    @ExcelProperty("盘库状态  1-未开始、2-盘库中、3-已结束")
    private Integer stocktakeStatus;

    @Schema(description = "预计盘点日期")
    @ExcelProperty("预计盘点日期")
    private LocalDateTime planDate;

    @Schema(description = "实际开始时间")
    @ExcelProperty("实际开始时间")
    private LocalDateTime actStartTime;

    @Schema(description = "实际结束时间")
    @ExcelProperty("实际结束时间")
    private LocalDateTime actEndTime;

    @Schema(description = "采购合同主键", example = "17341")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "22083")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    @ExcelProperty("销售合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "31460")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "张三")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "盘点人主键", example = "1814")
    @ExcelProperty("盘点人主键")
    private Long stocktakeUserId;

    @Schema(description = "盘点人姓名", example = "赵六")
    @ExcelProperty("盘点人姓名")
    private String stocktakeUserName;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    @ExcelProperty("打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "54")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "盘点单-明细")
    private List<StocktakeItemRespVO> stocktakeItemRespVOList;

    @Schema(description = "任务id")
    private String processInstanceId;
}
