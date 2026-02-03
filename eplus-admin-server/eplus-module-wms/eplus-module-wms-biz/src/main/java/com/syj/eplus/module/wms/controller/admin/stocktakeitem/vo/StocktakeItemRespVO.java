package com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-盘点单-明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StocktakeItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4363")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "盘点单-主键", example = "5150")
    @ExcelProperty("盘点单-主键")
    private Long stocktakeId;

    @Schema(description = "产品主键", example = "10504")
    @ExcelProperty("产品主键")
    private Long skuId;

    @Schema(description = "产品编码", example = "")
    @ExcelProperty("产品编码")
    private String skuCode;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "产品中文名称", example = "李四")
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

    @Schema(description = "仓库主键", example = "22582")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "存放位置")
    @ExcelProperty("存放位置")
    private String position;

    @Schema(description = "库存数量")
    @ExcelProperty("库存数量")
    private Integer stockQuantity;

    @Schema(description = "库存箱数")
    @ExcelProperty("库存箱数")
    private Integer stockBoxQuantity;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "盘点位置")
    @ExcelProperty("盘点位置")
    private String stocktakePosition;

    @Schema(description = "盘点数量")
    @ExcelProperty("盘点数量")
    private Integer stocktakeStockQuantity;

    @Schema(description = "盘点结果 1-盘盈 2-盘亏 3-盘平")
    @ExcelProperty("盘点结果")
    private Integer stocktakeStockResult;

    @Schema(description = "差异数量")
    @ExcelProperty("差异数量")
    private Integer  diffQuantity;

    @Schema(description = "盘点箱数")
    @ExcelProperty("盘点箱数")
    private Integer stocktakeStockBoxQuantity;

    @Schema(description = "供应商主键", example = "16329")
    @ExcelProperty("供应商主键")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "客户主键", example = "17435")
    @ExcelProperty("客户主键")
    private Long custId;

    @Schema(description = "客户编码", example = "31999")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "张三")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;

    @Schema(description = "归属公司主键", example = "5937")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

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

    @Schema(description = "采购员")
    private UserDept purchaseUser;

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
