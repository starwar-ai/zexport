package com.syj.eplus.module.wms.controller.admin.stock.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 仓储管理-库存明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockRespVO {

    @Schema(description = "库存主键", example = "31999")
    @ExcelProperty("库存主键")
    private Long id;

    @Schema(description = "客户主键", example = "31999")
    @ExcelProperty("客户主键")
    private Long custId;

    @Schema(description = "客户编码", example = "芋艿")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "芋艿")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;

    @Schema(description = "产品主键", example = "27637")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    @ExcelProperty("产品编码")
    private String skuCode;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "产品中文名称", example = "王五")
    @ExcelProperty("产品中文名称")
    private String skuName;

    @Schema(description = "自主品牌标识")
    @ExcelProperty("自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    @ExcelProperty("客户产品标识")
    private Integer custProFlag;
    /**
     * 在制数量(采购合同下单时库存)
     */
    @Schema(description = "在制数量")
    @ExcelProperty("在制数量")
    private Integer totalProducingQuantity;

    /**
     * 入库数量（批次入库时的库存）
     */
    @Schema(description = "入库数量")
    @ExcelProperty("入库数量")
    private Integer totalInitQuantity;
    /**
     * 出库数量（已出库库存汇总）
     */
    @Schema(description = "出库数量")
    @ExcelProperty("出库数量")
    private Integer totalUsedQuantity;
    /**
     * 锁定数量（锁定数量汇总）
     */
    @Schema(description = "锁定数量")
    @ExcelProperty("锁定数量")
    private Integer totalLockQuantity;
    /**
     * 可用数量（可用数量=在制数量+入库数量-出库数量-锁定数量）
     */
    @Schema(description = "可用数量")
    @ExcelProperty("可用数量")
    private Integer totalAvailableQuantity;
    /**
     * 剩余库存-动态计算（仓库中真实存在的库存， 剩余库存 = 在制数量+入库数量-出库数量/剩余库存=可用数量+锁定数量  无销售合同）
     */
    @Schema(description = "剩余库存")
    @ExcelProperty("剩余库存")
    private Integer totalRemainderQuantity;

//    @Schema(description = "外箱规格长度 cm")
//    @ExcelProperty("外箱规格长度 cm")
//    private BigDecimal outerboxLength;
//
//    @Schema(description = "外箱规格宽度 cm")
//    @ExcelProperty("外箱规格宽度 cm")
//    private BigDecimal outerboxWidth;
//
//    @Schema(description = "外箱规格高度 cm")
//    @ExcelProperty("外箱规格高度 cm")
//    private BigDecimal outerboxHeight;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "公司主键")
    @ExcelProperty("公司主键")
    private Long companyId;

    @Schema(description = "公司名称")
    @ExcelProperty("公司名称")
    private String companyName;

    @ExcelProperty("批次信息")
    private List<StockDetailRespVO> children;

    private UserDept purchaseUser;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

}
