package com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 仓储管理-盘库调整单-明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AdjustmentItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4669")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "调整单主键", example = "5580")
    @ExcelProperty("调整单主键")
    private Long adjustmentId;

    @Schema(description = "产品主键", example = "4758")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    @ExcelProperty("产品编码")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    @ExcelProperty("产品中文名称")
    private String skuName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @Schema(description = "自主品牌标识")
    @ExcelProperty("自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    @ExcelProperty("客户产品标识")
    private Integer custProFlag;

    @Schema(description = "产品序号")
    @ExcelProperty("产品序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    @ExcelProperty("来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "仓库主键", example = "3795")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "张三")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "存放位置")
    @ExcelProperty("存放位置")
    private String position;

    @Schema(description = "盘点位置")
    @ExcelProperty("盘点位置")
    private String stocktakePosition;

    @Schema(description = "库存数量")
    @ExcelProperty("库存数量")
    private Integer stockQuantity;

    @Schema(description = "盘点数量")
    @ExcelProperty("盘点数量")
    private Integer stocktakeStockQuantity;

    @Schema(description = "盘盈/盘亏数量")
    @ExcelProperty("盘盈/盘亏数量")
    private Integer differenceQuantity;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "库存箱数")
    @ExcelProperty("库存箱数")
    private Integer stockBoxQuantity;

    @Schema(description = "盘点箱数")
    @ExcelProperty("盘点箱数")
    private Integer stocktakeStockBoxQuantity;

    @Schema(description = "盘盈/盘亏箱数")
    @ExcelProperty("盘盈/盘亏箱数")
    private Integer differenceBoxQuantity;

    @Schema(description = "供应商主键", example = "31126")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "赵六")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "客户主键", example = "27245")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "芋艿")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;

    @Schema(description = "归属公司主键", example = "2507")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "芋艿")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "单价")
    private JsonAmount price;

    @Schema(description = "总价")
    private JsonAmount totalAmount;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "差异原因")
    private String diffReasons;
}
