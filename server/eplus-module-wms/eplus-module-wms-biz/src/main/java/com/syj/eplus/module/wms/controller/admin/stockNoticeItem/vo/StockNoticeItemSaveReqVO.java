package com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库通知单-明细新增/修改 Request VO")
@Data
public class StockNoticeItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10978")
    private Long id;

    @Schema(description = "入/出库通知单主键", example = "6479")
    private Long noticeId;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "来源单据明细序号")
    private Integer sourceSortNum;

    @Schema(description = "入/出库状态（未收/出货、部分收/出货、完全收/出货）", example = "1")
    private Integer noticeItemStatus;

    @Schema(description = "产品主键", example = "16463")
    private Long skuId;

    @Schema(description = "产品编码", example = "4758")
    private String skuCode;

    @Schema(description = "产品中文名称", example = "赵六")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "自主品牌标识")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标识")
    private Integer custProFlag;

    @Schema(description = "供应商主键", example = "5424")
    private Long venderId;

    @Schema(description = "供应商编码", example = "4758")
    private String venderCode;

    @Schema(description = "供应商名称", example = "芋艿")
    private String venderName;

    @Schema(description = "客户主键", example = "24865")
    private Long custId;

    @Schema(description = "客户编码", example = "4758")
    private String custCode;

    @Schema(description = "客户名称", example = "李四")
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

    @Schema(description = "总体积(单箱体积x箱数cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（单箱毛重x箱数 {数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "单托体积（cm³）")
    private BigDecimal palletVolume;

    @Schema(description = "单托毛重（{数量,单位}）")
    private JsonWeight palletWeight;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "待入库数量")
    private Integer pendingStockQuantity;

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
     * 转出入库单标识
     */
    private Integer convertBillFlag;
}
