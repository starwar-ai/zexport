package com.syj.eplus.module.wms.api.stock.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库单-明细新增/修改 Request VO")
@Data
public class BillItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26335")
    private Long id;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "来源单主键", example = "32336")
    private Long sourceId;

    @Schema(description = "来源单单号", example = "32336")
    private String sourceCode;

    @Schema(description = "来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库", example = "1")
    private Integer sourceType;

    @Schema(description = "入/出库类型 1-入库、2-出库", example = "1")
    private Integer billType;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "2")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "9587")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "芋艿")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "仓库主键", example = "2270")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "23271")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    private String venderCode;

    @Schema(description = "供应商名称", example = "赵六")
    private String venderName;

    @Schema(description = "客户主键", example = "13738")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    private String custCode;

    @Schema(description = "客户名称", example = "王五")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "应收/出数量")
    private Integer orderQuantity;

    @Schema(description = "应收/出箱数")
    private Integer orderBoxQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "实收/出数量")
    private Integer actQuantity;

    @Schema(description = "实收/出箱数")
    private Integer actBoxQuantity;

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "归属公司主键", example = "28431")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "张三")
    private String companyName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "唯一编号")
    private String uniqueCode;

    @Schema(description = "来源编号")
    private String sourceUniqueCode;

    @Schema(description = "异常状态")
    private Integer abnormalStatus;

    @Schema(description = "异常说明")
    private String abnormalRemark;

    @Schema(description = "采购合同主键", example = "5957")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "采购员主键", example = "23823")
    private Long purchaserId;

    @Schema(description = "采购员部门主键", example = "15257")
    private Long purchaserDeptId;

    @Schema(description = "待入库数量")
    private Integer pendingStockQuantity;

    @Schema(description = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    @Schema(description = "业务员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    @Schema(description = "采购合同明细编号")
    private String purchaseItemUniqueCode;

    @Schema(description = "价格")
    private JsonAmount price;

    @Schema(description = "销售合同明细编号")
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
     * 发货地点
     */
    private Integer shippingAddress;

    /**
     * 自营货号
     */
    private String oskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;
}
