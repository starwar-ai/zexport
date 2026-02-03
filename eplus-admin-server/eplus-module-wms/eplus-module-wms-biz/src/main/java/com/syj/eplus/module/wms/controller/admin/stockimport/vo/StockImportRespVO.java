package com.syj.eplus.module.wms.controller.admin.stockimport.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 仓储管理-库存明细导入 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockImportRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16244")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "是否分箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否分箱")
    private Integer splitBoxFlag;

    @Schema(description = "规格", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "转拉柜数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("转拉柜数量")
    private Integer cabinetQuantity;

    @Schema(description = "采购员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采购员")
    private UserDept purchaseUser;

    @Schema(description = "基础产品编号")
    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @Schema(description = "盘点差异数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("盘点差异数量")
    private Integer diffQuantity;

    @Schema(description = "剩余总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("剩余总金额")
    private JsonAmount remainTotalAmount;

    @Schema(description = "在制数量(采购合同下单时库存)")
    @ExcelProperty("在制数量(采购合同下单时库存)")
    private Integer producingQuantity;

    @Schema(description = "入库单-主键", example = "10966")
    @ExcelProperty("入库单-主键")
    private Long billId;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "入库单明细-主键", example = "1016")
    @ExcelProperty("入库单明细-主键")
    private Long billItemId;

    @Schema(description = "产品主键", example = "24116")
    @ExcelProperty("产品主键")
    private Long skuId;

    @Schema(description = "SKU编号")
    @ExcelProperty("SKU编号")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "张三")
    @ExcelProperty("产品中文名称")
    private String skuName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    @ExcelProperty("自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    @ExcelProperty("客户产品标识")
    private Integer custProFlag;

    @Schema(description = "入库时间")
    @ExcelProperty("入库时间")
    private LocalDateTime receiptTime;

    @Schema(description = "入库库存（批次入库时的库存）")
    @ExcelProperty("入库库存（批次入库时的库存）")
    private Integer initQuantity;

    @Schema(description = "出库数量（已出库库存汇总）")
    @ExcelProperty("出库数量（已出库库存汇总）")
    private Integer usedQuantity;

    @Schema(description = "锁定数量（锁定数量汇总）")
    @ExcelProperty("锁定数量（锁定数量汇总）")
    private Integer lockQuantity;

    @Schema(description = "可用数量")
    @ExcelProperty("可用数量")
    private Integer availableQuantity;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "6159")
    @ExcelProperty("价格")
    private JsonAmount price;

    @Schema(description = "总金额")
    @ExcelProperty("总金额")
    private JsonAmount totalAmount;

    @Schema(description = "采购合同主键", example = "11026")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "17032")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同编号")
    @ExcelProperty("销售合同编号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "25401")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "赵六")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "存放位置")
    @ExcelProperty("存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "23209")
    @ExcelProperty("供应商主键")
    private Long venderId;

    @Schema(description = "供应商编码")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "李四")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "客户主键", example = "20410")
    @ExcelProperty("客户主键")
    private Long custId;

    @Schema(description = "客户编码")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "赵六")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;

    @Schema(description = "外箱规格长度 cm")
    @ExcelProperty("外箱规格长度 cm")
    private BigDecimal outerboxLength;

    @Schema(description = "外箱规格宽度 cm")
    @ExcelProperty("外箱规格宽度 cm")
    private BigDecimal outerboxWidth;

    @Schema(description = "外箱规格高度 cm")
    @ExcelProperty("外箱规格高度 cm")
    private BigDecimal outerboxHeight;

    @Schema(description = "单箱体积（cm³）")
    @ExcelProperty("单箱体积（cm³）")
    private BigDecimal outerboxVolume;

    @Schema(description = "单箱毛重（{数量,单位}）")
    @ExcelProperty("单箱毛重（{数量,单位}）")
    private JsonWeight outerboxGrossweight;

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

    @Schema(description = "归属公司主键", example = "21361")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "王五")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "失败原因", example = "你猜")
    @ExcelProperty("失败原因")
    private String errorRemark;

    @Schema(description = "失败原因")
    @ExcelProperty("失败原因")
    private Integer errorFlag;

    @Schema(description = "导入批次号")
    @ExcelProperty("导入批次号")
    private String importCode;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}