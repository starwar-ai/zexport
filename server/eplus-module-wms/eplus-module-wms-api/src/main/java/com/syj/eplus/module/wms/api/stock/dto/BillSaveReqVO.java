package com.syj.eplus.module.wms.api.stock.dto;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库单新增/修改 Request VO")
@Data
public class BillSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "23325")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "入/出库类型 1-入库单、2-出库单", example = "2")
    private Integer billType;

    @Schema(description = "单据状态1-未确认 2-已确认 3-作废", example = "1")
    private Integer billStatus;

    @Schema(description = "入/出库通知单号")
    private String noticeCode;

    @Schema(description = "销售合同主键", example = "14862")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "入/出库时间")
    private LocalDateTime billTime;

    @Schema(description = "销售人员主键", example = "5333")
    private Long salesId;

    @Schema(description = "销售员部门主键", example = "19929")
    private Long salesDeptId;

    @Schema(description = "仓库主键", example = "23417")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "25234")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "仓储管理-入(出)库单-明细列表")
    private List<BillItemSaveReqVO> billItemSaveReqVOList;

    @Schema(description = "仓储管理-入(出)库单-锁定列表")
    private List<StockLockSaveReqVO> stockLockSaveReqVOList;

    @Schema(description = "供应商库存标识")
    private Boolean venderStockFlag;

    @Schema(description = "图片")
    private List<SimpleFile> pictures;

    @Schema(description = "来源单类型")
    private Integer sourceType;

    @Schema(description = "来源单编号")
    private String sourceCode;

    @Schema(description = "来源单主键")
    private Long sourceId;

    @Schema(description = "出运发票号")
    private String invoiceCode;

    @Schema(description = "是否仓库发货标识")
    private Integer factoryFlag;
}
