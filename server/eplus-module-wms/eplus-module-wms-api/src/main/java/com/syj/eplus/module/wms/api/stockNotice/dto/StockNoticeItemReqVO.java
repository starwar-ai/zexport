package com.syj.eplus.module.wms.api.stockNotice.dto;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Desc——采购合同转入库通知单-请求主表
 * Create by Rangers at  2024-06-11 22:13
 */
@Data
public class StockNoticeItemReqVO {

    /**
     * 来源单据明细序号
     */
    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    /**
     * 仓库主键
     */
    @Schema(description = "仓库主键", example = "10095")
    private Long warehouseId;


    /**
     * 仓库名称
     */
    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;



    /**
     * 仓库联系人
     */
    @Schema(description = "仓库联系人主键", example = "10095")
    private Long warehouseUserId;
    /**
     * 仓库联系人电话
     */
    @Schema(description = "仓库联系人名称", example = "10095")
    private String warehouseUserName;

    /**
     * 仓库联系人电话
     */
    @Schema(description = "仓库联系人电话", example = "10095")
    private String warehouseUserPhone;

    /**
     * 产品主键
     */
    @Schema(description = "产品主键")
    private Long skuId;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String skuCode;

    /**
     * 供应商主键
     */
    @Schema(description = "供应商主键")
    private Long venderId;

    /**
     * 客户主键
     */
    @Schema(description = "客户主键")
    private Long custId;


    /**
     * 应收/出数量
     */
    @Schema(description = "应收/出数量")
    private Integer orderQuantity;

    /**
     * 应收/出箱数
     */
    @Schema(description = "应收/出箱数")
    private Integer orderBoxQuantity;

    @Schema(description = "内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    /**
     * 明细总体积(单箱体积x箱数cm³)
     */
    private BigDecimal totalVolume;
    /**
     * 明细总毛重（单箱毛重x箱数 {数量,单位}）
     */
    private JsonWeight totalWeight;
    /**

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 唯一编号
     */
    private String uniqueCode;

    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 采购合同主键
     */
    @Schema(description = "采购合同主键", example = "6613")
    private Long purchaseContractId;

    /**
     * 采购合同号
     */
    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    /**
     * 采购员主键
     */
    @Schema(description = "采购员主键", example = "14452")
    private Long purchaserId;

    /**
     * 采购员名称
     */
    @Schema(description = "采购员名称")
    private String purchaseUserName;

    /**
     * 采购员部门主键
     */
    @Schema(description = "采购员部门主键", example = "11021")
    private Long purchaserDeptId;

    /**
     * 采购员部门名称
     */
    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;

    /**
     * 采购合同明细编号
     */
    private String purchaseItemUniqueCode;

    /**
     * 销售合同明细编号
     */
    private String saleItemUniqueCode;


    /**
     * 实际入库数量
     */
    private Integer realBillQuantity;

    /**
     * 待入库数量
     */
    private Integer pendingStockQuantity;

    /**
     * 跟单员
     */
    private UserDept manager;

    private UserDept sales;

    private String cskuCode;

    private String basicSkuCode;

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

    /**
     * 客户po号
     */
    private String custPo;
}
