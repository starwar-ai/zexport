package com.syj.eplus.module.wms.api.stock.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-库存明细 Response VO")
@Data
public class StockDetailRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8035")
    private Long id;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "入库单明细-主键", example = "19031")
    private Long billItemId;

    @Schema(description = "产品主键", example = "27637")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "王五")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "图片")
    private SimpleFile mainPicture;

    @Schema(description = "入库时间")
    private LocalDateTime receiptTime;
    /**
     * 在制数量(采购合同下单时库存)
     */
    @Schema(description = "在制数量")
    private Integer producingQuantity;

    /**
     * 入库数量（批次入库时的库存）
     */
    @Schema(description = "入库数量")
    private Integer initQuantity;
    /**
     * 出库数量（已出库库存汇总）
     */
    @Schema(description = "出库数量")
    private Integer usedQuantity;
    /**
     * 锁定数量（锁定数量汇总）
     */
    @Schema(description = "锁定数量")
    private Integer lockQuantity;

    @Schema(description = "销售合同-锁定数量")
    private Integer sourceOrderLockedQuantity;
    /**
     * 可用数量（可用数量=在制数量+入库数量-出库数量-锁定数量）
     */
    @Schema(description = "可用数量")
    private Integer availableQuantity;
    /**
     * 剩余库存-动态计算（仓库中真实存在的库存， 剩余库存 = 入库数量-出库数量/剩余库存=可用数量+锁定数量  无销售合同）
     */
    @Schema(description = "剩余库存")
    private Integer remainderQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "单价", example = "6033")
    private JsonAmount price;

    @Schema(description = "总金额")
    private JsonAmount totalAmount;

    @Schema(description = "采购合同主键", example = "1188")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "外销合同主键", example = "31019")
    private Long saleContractId;

    @Schema(description = "外销合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "4730")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "芋艿")
    private String warehouseName;

    @Schema(description = "存放位置")
    private String position;

    @Schema(description = "供应商主键", example = "27703")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "客户主键", example = "31999")
    private Long custId;

    @Schema(description = "客户编码", example = "31999")
    private String custCode;

    @Schema(description = "客户名称", example = "芋艿")
    private String custName;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "归属公司主键", example = "21354")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "芋艿")
    private String companyName;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "供应商仓标识（0-否 1-是）", example = "")
    private Integer venderFlag;

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
