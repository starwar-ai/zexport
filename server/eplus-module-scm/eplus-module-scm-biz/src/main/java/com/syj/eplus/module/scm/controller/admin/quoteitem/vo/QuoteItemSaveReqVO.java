package com.syj.eplus.module.scm.controller.admin.quoteitem.vo;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 供应商报价明细新增/修改 Request VO")
@Data
public class QuoteItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1934")
    private Long id;

    @Schema(description = "供应商报价单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "22266")
    private Long quoteId;

    @Schema(description = "SKU编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuCode;

    @Schema(description = "供应商主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderId;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderName;

    @Schema(description = "采购员id", example = "7609")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "20825")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderProdCode;

    @Schema(description = "报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer freightFlag;

    @Schema(description = "是否含包装", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    private List<Long> packageType;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "是否含税", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer faxFlag;

    @Schema(description = "税率", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal taxRate;

    @Schema(description = "最小起购量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer moq;

    @Schema(description = "包装价", requiredMode = Schema.RequiredMode.REQUIRED, example = "6631")
    private JsonAmount packagingPrice;

    @Schema(description = "运费", requiredMode = Schema.RequiredMode.REQUIRED, example = "17301")
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "1396")
    private JsonAmount unitPrice;

    @Schema(description = "总价", requiredMode = Schema.RequiredMode.REQUIRED, example = "7318")
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "12952")
    private JsonAmount withTaxPrice;

    @Schema(description = "交期")
    private Integer delivery;

    @Schema(description = "采购链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    private String purchaseUrl;

    @Schema(description = "内箱装量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal packageHeight;

    @Schema(description = "单品毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonWeight singleGrossweight;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer defaultFlag;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "开票品名")
    private String invoiceName;

    @Schema(description = "40尺柜装量")
    private Long fortyFootContainerCapacity;

    @Schema(description = "40高柜装量")
    private Long fortyFootHighContainerCapacity;

    @Schema(description = "20尺柜装量")
    private Long twentyFootContainerCapacity;

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