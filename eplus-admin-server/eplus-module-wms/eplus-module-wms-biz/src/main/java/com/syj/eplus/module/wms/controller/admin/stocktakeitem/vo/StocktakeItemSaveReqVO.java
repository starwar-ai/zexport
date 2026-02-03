package com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 仓储管理-盘点单-明细新增/修改 Request VO")
@Data
public class StocktakeItemSaveReqVO {

    @Schema(description = "主键", example = "4363")
    private Long id;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "盘点单-主键", example = "5150")
    private Long stocktakeId;

    @Schema(description = "产品主键", example = "10504")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "李四")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "仓库主键", example = "22582")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "库存数量")
    private Integer stockQuantity;

    @Schema(description = "库存箱数")
    private Integer stockBoxQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "盘点位置")
    private String stocktakePosition;

    @Schema(description = "盘点数量")
    private Integer stocktakeStockQuantity;

    @Schema(description = "盘点箱数")
    private Integer stocktakeStockBoxQuantity;

    @Schema(description = "供应商主键", example = "16329")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "客户主键", example = "17435")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "归属公司主键", example = "5937")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    private String companyName;

    @Schema(description = "备注", example = "随便")
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

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}
