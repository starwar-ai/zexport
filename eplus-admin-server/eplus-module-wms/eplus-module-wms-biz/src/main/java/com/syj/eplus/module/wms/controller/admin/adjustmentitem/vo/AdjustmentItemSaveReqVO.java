package com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 仓储管理-盘库调整单-明细新增/修改 Request VO")
@Data
public class AdjustmentItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4669")
    private Long id;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "调整单主键", example = "5580")
    private Long adjustmentId;

    @Schema(description = "产品主键", example = "4758")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "产品序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "仓库主键", example = "3795")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "张三")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "盘点位置")
    private String stocktakePosition;

    @Schema(description = "库存数量")
    private Integer stockQuantity;

    @Schema(description = "盘点数量")
    private Integer stocktakeStockQuantity;

    @Schema(description = "盘盈/盘亏数量")
    private Integer differenceQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "库存箱数")
    private Integer stockBoxQuantity;

    @Schema(description = "盘点箱数")
    private Integer stocktakeStockBoxQuantity;

    @Schema(description = "盘盈/盘亏箱数")
    private Integer differenceBoxQuantity;

    @Schema(description = "供应商主键", example = "31126")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    private String venderCode;

    @Schema(description = "供应商名称", example = "赵六")
    private String venderName;

    @Schema(description = "客户主键", example = "27245")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    private String custCode;

    @Schema(description = "客户名称", example = "芋艿")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "归属公司主键", example = "2507")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "芋艿")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "单价")
    private JsonAmount price;

    @Schema(description = "总价")
    private JsonAmount totalAmount;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "差异原因")
    private String diffReasons;

}
