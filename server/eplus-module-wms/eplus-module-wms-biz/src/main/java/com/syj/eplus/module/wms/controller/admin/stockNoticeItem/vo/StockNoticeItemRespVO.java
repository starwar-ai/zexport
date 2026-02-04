package com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库通知单-明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockNoticeItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10978")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "入/出库通知单主键", example = "6479")
    @ExcelProperty("入/出库通知单主键")
    private Long noticeId;

    @Schema(description = "批次号", example = "6479")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    @ExcelProperty("来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "1")
    @ExcelProperty("入/出库状态（未收/出货、部分收/出货、完全收/出货）")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "16463")
    private Long skuId;

    @Schema(description = "产品编码", example = "")
    @ExcelProperty("产品编码")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "赵六")
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

    @Schema(description = "供应商主键", example = "5424")
    @ExcelProperty("供应商主键")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    @ExcelProperty("供应商编码")
    private String venderCode;

    @Schema(description = "供应商名称", example = "芋艿")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "客户主键", example = "24865")
    @ExcelProperty("客户主键")
    private Long custId;

    @Schema(description = "客户编码", example = "31999")
    @ExcelProperty("客户编码")
    private String custCode;

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

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "唯一编号")
    private String uniqueCode;


    @Schema(description = "来源编号")
    private String sourceUniqueCode;

    @Schema(description = "采购合同主键", example = "5957")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @Schema(description = "采购员主键", example = "23823")
    private Long purchaserId;

    @ExcelProperty("采购员姓名")
    private String purchaseUserName;

    @ExcelProperty("采购员电话")
    private String purchaseMobile;

    @Schema(description = "采购员部门主键", example = "15257")
    private Long purchaserDeptId;

    @ExcelProperty("采购员部门")
    private String purchaseUserDeptName;

    @Schema(description = "待入库数量")
    private Integer pendingStockQuantity;

    @Schema(description = "仓库主键", example = "10095")
    @ExcelProperty("仓库主键")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    @ExcelProperty("仓库名称")
    private String warehouseName;

    @Schema(description = "业务员", example = "王五")
    private UserDept sales;

    @Schema(description = "跟单员", example = "王五")
    private UserDept manager;

    @Schema(description = "采购合同明细编号", example = "王五")
    private String purchaseItemUniqueCode;

    @Schema(description = "实际入库数量", example = "王五")
    private Integer realBillQuantity;

    @Schema(description = "销售合同明细编号", example = "王五")
    private String saleItemUniqueCode;

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

    /**
     * 来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库
     *
     * 枚举 {@link TODO stock_source_type 对应的类}
     */
    private Integer sourceType;


    private Integer shippedAddress;

    @Schema(description = "转出入库单标识")
    private Integer convertBillFlag;

    @Schema(description = "可出库数量")
    private Integer outQuantity;

    @Schema(description = "库存明细id")
    private Long stockId;
}
