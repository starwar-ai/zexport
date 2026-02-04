package com.syj.eplus.module.wms.controller.admin.billitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库单-明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class BillItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26335")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "来源单主键", example = "32336")
    @ExcelProperty("来源单主键")
    private Long sourceId;

    @Schema(description = "来源单单号", example = "32336")
    @ExcelProperty("来源单单号")
    private String sourceCode;

    @Schema(description = "来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库", example = "1")
    @ExcelProperty("来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库")
    private Integer sourceType;

    @Schema(description = "入/出库类型 1-入库、2-出库")
    @ExcelProperty("入/出库类型 1-入库、2-出库")
    private Integer billType;

    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    @ExcelProperty("来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "2")
    @ExcelProperty("入/出库状态（未收/出货、部分收/出货、完全收/出货）")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "9587")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    @ExcelProperty("产品编码")
    private String skuCode;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "产品中文名称", example = "芋艿")
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

    @Schema(description = "仓库主键", example = "2270")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "存放位置")
    @ExcelProperty("存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "23271")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "赵六")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "客户主键", example = "13738")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    @ExcelProperty("客户编码")
    private String custCode;

    @Schema(description = "客户名称", example = "王五")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户PO号")
    @ExcelProperty("客户PO号")
    private String custPo;

    @Schema(description = "应收/出数量")
    @ExcelProperty("应收/出数量")
    private Integer orderQuantity;

    @Schema(description = "应收/出箱数")
    @ExcelProperty("应收/出箱数")
    private Integer orderBoxQuantity;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    @ExcelProperty("内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "实收/出数量")
    @ExcelProperty("实收/出数量")
    private Integer actQuantity;

    @Schema(description = "实收/出箱数")
    @ExcelProperty("实收/出箱数")
    private Integer actBoxQuantity;

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

    @Schema(description = "归属公司主键", example = "28431")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "张三")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "操作人主键", example = "1024")
    @ExcelProperty("操作人主键")
    private String creator;

    @Schema(description = "操作人姓名", example = " xx")
    @ExcelProperty("操作人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "唯一编号")
    private String uniqueCode;

    @Schema(description = "来源编号")
    private String sourceUniqueCode;


    @Schema(description = "异常状态")
    private Integer abnormalStatus;

    @Schema(description = "异常说明")
    private String abnormalRemark;

    @Schema(description = "采购合同主键", example = "5957")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "采购员主键", example = "23823")
    private Long purchaserId;

    @ExcelProperty("采购员")
    private String purchaserName;

    @Schema(description = "采购员部门主键", example = "15257")
    private Long purchaserDeptId;

    @ExcelProperty("采购员部门")
    private String purchaserDeptName;

    @Schema(description = "跟单员", example = "王五")
    private UserDept manager;

    @Schema(description = "业务员", example = "王五")
    private UserDept sales;

    /**
     * 采购合同明细编号
     */
    private String purchaseItemUniqueCode;

    private String saleItemUniqueCode;

    @Schema(description = "单价")
    private JsonAmount price;

    @Schema(description = "出运明细id")
    private Long shipmentItemId;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;

    @Schema(description = "缩略图")
    private String thumbnail;
}
