package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购合同明细新增/修改 Request VO")
@Data
public class SavePurchaseContractItemReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11767")
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "skuid", example = "31593")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户id", example = "18970")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "采购数量")
    private Integer quantity;

    @Schema(description = "采购含税金额")
    private JsonAmount withTaxTotal;

    @Schema(description = "供应商id", example = "27269")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "预计到料日期")
    private LocalDateTime planArriveDate;

    @Schema(description = "验货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "验货状态不能为空")
    private Integer checkStatus;

    @Schema(description = "已验货数量")
    private Integer checkedQuantity;

    @Schema(description = "收货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "收货状态不能为空")
    private Integer receiveStatus;

    @Schema(description = "已收货数量")
    private Integer receivedQuantity;

    @Schema(description = "退货量")
    private Integer exchangeQuantity;

    @Schema(description = "换货量")
    private Integer returnQuantity;

    @Schema(description = "采购合同单号", example = "15766")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "是否赠品")
    private Integer freeFlag;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    private Integer packageUnit;


    @Schema(description = "是否同步供应商报价")
    private Integer syncQuoteFlag;

    @Schema(description = "单品毛重")
    private JsonWeight singleGrossweight;

    @Schema(description = "包装价")
    private JsonAmount packagingPrice;

    @Schema(description = "运费")
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价")
    private JsonAmount unitPrice;

    @Schema(description = "总价")
    private JsonAmount totalPrice;

    @Schema(description = "含税总价")
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "采购类型不能为空")
    private Integer purchaseType;

    @Schema(description = "采购员id", example = "17679")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "张三")
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "11166")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门不名称", example = "王五")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    private List<Long> packageType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否含包装")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    private Integer moq;

    @Schema(description = "箱数", example = "3726")
    private Integer boxCount;

    @Schema(description = "入库状态", example = "2")
    private Integer warehousingType;

    @Schema(description = "仓库id列表")
    private String wmsIds;

    @Schema(description = "仓库名称列表")
    private String wmsNames;

    @Schema(description = "交期")
    private Integer delivery;

    @Schema(description = "采购链接")
    private String purchaseUrl;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "是否通用辅料")
    private Integer auxiliarySkuFlag;

    @Schema(description = "辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "规格描述")
    private String specRemark;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    @Schema(description = "辅料属于的采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的采购合同产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料属于的采购合同产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料属于的采购合同产品客户货号")
    private String auxiliaryCskuCode;

    @Schema(description = "计量单位")
    private String skuUnit;

    @Schema(description = "计量单位")
    private Integer measureUnit;

    @Schema(description = "自营产品标记")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标记")
    private Integer custProFlag;

    @Schema(description = "包装费用")
    private JsonAmount packageCost;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "销售合同主键")
    private Long saleContractId;

    @Schema(description = "销售合同编码")
    private String saleContractCode;

    @Schema(description = "销售合同明细编号")
    private String saleItemUniqueCode;

    @Schema(description = "唯一标识")
    private String uniqueCode;

    @Schema(description = "")
    private String sourceUniqueCode;

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
