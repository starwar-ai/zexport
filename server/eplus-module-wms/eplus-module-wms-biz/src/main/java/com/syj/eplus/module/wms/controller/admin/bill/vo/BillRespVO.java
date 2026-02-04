package com.syj.eplus.module.wms.controller.admin.bill.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class BillRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "23325")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单号")
    private String code;

    @Schema(description = "入/出库类型 1-入库单、2-出库单", example = "2")
    @ExcelProperty("入/出库类型 1-入库单、2-出库单")
    private Integer billType;

    @Schema(description = "单据状态1-未确认 2-已确认 3-作废", example = "1")
    @ExcelProperty("单据状态1-未确认 2-已确认 3-作废")
    private Integer billStatus;

    @Schema(description = "入/出库通知单号")
    @ExcelProperty("入/出库通知单号")
    private String noticeCode;

    @Schema(description = "销售合同主键", example = "14862")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    @ExcelProperty("销售合同号")
    private String saleContractCode;

    @Schema(description = "入/出库时间")
    @ExcelProperty("入/出库时间")
    private LocalDateTime billTime;

    @Schema(description = "仓库主键", example = "23417")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    @ExcelProperty("打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "归属公司主键", example = "25234")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("创建人")
    @CompareField
    private String creator;

    @ExcelProperty("创建人姓名")
    @CompareField
    private String creatorName;

    @ExcelProperty("创建人部门名称")
    @CompareField
    private String creatorDeptName;

    private List<BillItemRespVO> children;

    @Schema(description = "图片")
    private List<SimpleFile> pictures;

    @Schema(description = "来源单类型")
    private Integer sourceType;

    @Schema(description = "来源单编号")
    private String sourceCode;

    @Schema(description = "来源单主键")
    private Long sourceId;

    @Schema(description = "创建人")
    private UserDept createUser;

    @Schema(description = "出运发票号")
    private String invoiceCode;
}
