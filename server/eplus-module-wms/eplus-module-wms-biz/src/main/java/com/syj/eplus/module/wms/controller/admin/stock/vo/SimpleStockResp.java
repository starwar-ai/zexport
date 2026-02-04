package com.syj.eplus.module.wms.controller.admin.stock.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-库存明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SimpleStockResp {
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

    private UserDept purchaseUser;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "入库单明细-主键", example = "19031")
    @ExcelProperty("入库单明细-主键")
    private Long billItemId;

    @Schema(description = "入库时间")
    @ExcelProperty("入库时间")
    private LocalDateTime receiptTime;
    /**
     * 在制数量(采购合同下单时库存)
     */
    @Schema(description = "在制数量")
    @ExcelProperty("在制数量")
    private Integer producingQuantity;

    /**
     * 入库数量（批次入库时的库存）
     */
    @Schema(description = "入库数量")
    @ExcelProperty("入库数量")
    private Integer initQuantity;
    /**
     * 出库数量（已出库库存汇总）
     */
    @Schema(description = "出库数量")
    @ExcelProperty("出库数量")
    private Integer usedQuantity;
    /**
     * 锁定数量（锁定数量汇总）
     */
    @Schema(description = "锁定数量")
    @ExcelProperty("锁定数量")
    private Integer lockQuantity;

    @Schema(description = "销售合同-锁定数量")
    @ExcelProperty("销售合同-锁定数量")
    private Integer sourceOrderLockedQuantity;
    /**
     * 可用数量（可用数量=在制数量+入库数量-出库数量-锁定数量）
     */
    @Schema(description = "可用数量")
    @ExcelProperty("可用数量")
    private Integer availableQuantity;
    /**
     * 剩余库存-动态计算（仓库中真实存在的库存， 剩余库存 = 入库数量-出库数量/剩余库存=可用数量+锁定数量  无销售合同）
     */
    @Schema(description = "剩余库存")
    @ExcelProperty("剩余库存")
    private Integer remainderQuantity;

    @Schema(description = "单价", example = "6033")
    @ExcelProperty("单价")
    private JsonAmount price;

    @Schema(description = "总金额")
    @ExcelProperty("总金额")
    private JsonAmount totalAmount;

    @Schema(description = "剩余总金额")
    @ExcelProperty("剩余总金额")
    private JsonAmount remainTotalAmount;

    @Schema(description = "采购合同主键", example = "1188")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "外销合同主键", example = "31019")
    @ExcelProperty("外销合同主键")
    private Long saleContractId;

    @Schema(description = "外销合同号")
    @ExcelProperty("外销合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "4730")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "芋艿")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "存放位置")
    @ExcelProperty("存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "27703")
    @ExcelProperty("供应商主键")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    @ExcelProperty("总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    @ExcelProperty("总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    @ExcelProperty("单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    @ExcelProperty("单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "供应商仓标识（0-否 1-是）", example = "")
    @ExcelProperty("供应商仓标识（0-否 1-是）")
    private Integer venderFlag;


    @Schema(description = "销售合同锁定库存")
    private Integer saleContractLockQuantity;

    @Schema(description = "差异数量")
    private Integer diffQuantity;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    @Schema(description = "可出库数量")
    private Integer outQuantity;
}
