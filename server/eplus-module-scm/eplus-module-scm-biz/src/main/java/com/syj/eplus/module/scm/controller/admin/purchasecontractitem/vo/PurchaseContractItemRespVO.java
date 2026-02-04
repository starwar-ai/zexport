package com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购合同明细 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class PurchaseContractItemRespVO implements ChangeExInterface {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11767")
    @ExcelProperty("主键")
    @CompareField
    private Long id;

    @Schema(description = "版本")
    @ExcelProperty("版本")
    @CompareField
    private Integer ver;

    @Schema(description = "序号")
    @ExcelProperty("序号")
    @CompareField
    private Integer sortNum;

    @Schema(description = "skuid", example = "31593")
    @ExcelProperty("skuid")
    @CompareField
    private Long skuId;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    @CompareField
    private String skuCode;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    @CompareField
    private String skuName;

    @Schema(description = "客户id", example = "18970")
    @ExcelProperty("客户ID")
    @CompareField
    private Long custId;

    @Schema(description = "客户编号")
    @ExcelProperty("客户编号")
    @CompareField
    private String custCode;

    @Schema(description = "客户名称")
    @ExcelProperty("客户名称")
    @CompareField
    private String custName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    @CompareField
    private String cskuCode;

    @Schema(description = "基础产品编号")
    @ExcelProperty("基础产品编号")
    @CompareField
    private String basicSkuCode;

    @Schema(description = "采购数量")
    @ExcelProperty("采购数量")
    @CompareField
    private Integer quantity;

    @Schema(description = "是否赠品")
    @ExcelProperty("是否赠品")
    @CompareField
    private Integer freeFlag;

    @Schema(description = "供应商id", example = "27269")
    @ExcelProperty("供应商id")
    @CompareField
    private Long venderId;

    @Schema(description = "供应商编号")
    @ExcelProperty("供应商编号")
    @CompareField
    private String venderCode;

    @Schema(description = "计划到料日期")
    @ExcelProperty("计划到料日期")
    @CompareField
    private LocalDateTime planArriveDate;

    @Schema(description = "验货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("验货状态")
    @CompareField
    private Integer checkStatus;

    @Schema(description = "已验货数量")
    @ExcelProperty("已验货数量")
    @CompareField
    private Integer checkedQuantity;

    @Schema(description = "验收状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("验收状态")
    @CompareField
    private Integer receiveStatus;

    @Schema(description = "已收货数量")
    @ExcelProperty("已收货数量")
    @CompareField
    private Integer receivedQuantity;

    @Schema(description = "退货量")
    @ExcelProperty("退货量")
    @CompareField
    private Integer exchangeQuantity;

    @Schema(description = "换货量")
    @ExcelProperty("换货量")
    @CompareField
    private Integer returnQuantity;

    @Schema(description = "采购合同单号", example = "15766")
    @ExcelProperty("采购合同单号")
    @CompareField
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    @CompareField
    private String purchaseContractCode;

    @Schema(description = "内箱装量")
    @ExcelProperty("内箱装量")
    @CompareField
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    @CompareField
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    @ExcelProperty("包装规格长度")
    @CompareField
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    @ExcelProperty("包装规格宽度")
    @CompareField
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    @ExcelProperty("包装规格高度")
    @CompareField
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    @ExcelProperty("包装规格单位")
    @CompareField
    private Integer packageUnit;

    @Schema(description = "外箱规格长度")
    @ExcelProperty("外箱规格长度")
    @CompareField
    private BigDecimal outerboxLength;

    @Schema(description = "外箱规格宽度")
    @ExcelProperty("外箱规格宽度")
    @CompareField
    private BigDecimal outerboxWidth;

    @Schema(description = "外箱规格高度")
    @ExcelProperty("外箱规格高度")
    @CompareField
    private BigDecimal outerboxHeight;

    @Schema(description = "外箱规格单位")
    @ExcelProperty("外箱规格单位")
    @CompareField
    private Integer outerboxUnit;

    @Schema(description = "外箱体积")
    @ExcelProperty("外箱体积")
    @CompareField
    private BigDecimal outerboxVolume;

    @Schema(description = "外箱净重")
    @ExcelProperty("外箱净重")
    @CompareField
    private JsonWeight outerboxNetweight;

    @Schema(description = "单品毛重")
    @ExcelProperty("单品毛重")
    @CompareField
    private JsonWeight singleGrossweight;

    @Schema(description = "外箱毛重")
    @ExcelProperty("外箱毛重")
    @CompareField
    private JsonWeight outerboxGrossweight;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;


    @Schema(description = "包装价", example = "17301")
    @ExcelProperty(value = "包装价", converter = AmountConvert.class)
    @CompareField
    private JsonAmount packagingPrice;

    @Schema(description = "运费", example = "17301")
    @ExcelProperty(value = "运费", converter = AmountConvert.class)
    @CompareField
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "31058")
    @ExcelProperty(value = "采购单价", converter = AmountConvert.class)
    @CompareField
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "4346")
    @ExcelProperty(value = "总价", converter = AmountConvert.class)
    @CompareField
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "22972")
    @ExcelProperty(value = "含税总价", converter = AmountConvert.class)
    @CompareField
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    @CompareField
    private BigDecimal taxRate;

    @Schema(description = "采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("采购类型")
    @CompareField
    private Integer purchaseType;

    @Schema(description = "采购员id", example = "17679")
    @ExcelProperty("采购员id")
    @CompareField
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "张三")
    @ExcelProperty("采购员姓名")
    @CompareField
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "11166")
    @ExcelProperty("采购员部门id")
    @CompareField
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门不名称", example = "王五")
    @ExcelProperty("采购员部门不名称")
    @CompareField
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    @ExcelProperty("工厂货号")
    @CompareField
    private String venderProdCode;

    @Schema(description = "报价日期")
    @ExcelProperty("报价日期")
    @CompareField
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    @ExcelProperty("是否含运费")
    @CompareField
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    @ExcelProperty("是否含包装")
    @CompareField
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    @ExcelProperty("包装方式")
    @CompareField
    private List<Long> packageType;


    @Schema(description = "包装方式中文名", example = "1")
    @ExcelProperty("包装方式中文名")
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    @ExcelProperty("包装方式英文名")
    private String packageTypeEngName;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @CompareField
    private String currency;

    @Schema(description = "是否含税")
    @ExcelProperty("是否含税")
    @CompareField
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    @ExcelProperty("最小起购量")
    @CompareField
    private Integer moq;

    @Schema(description = "箱数", example = "3726")
    @ExcelProperty("箱数")
    @CompareField
    private Integer boxCount;

    @Schema(description = "入库状态", example = "2")
    @ExcelProperty("入库状态")
    @CompareField
    private Integer warehousingType;

    @Schema(description = "是否同步供应商报价", example = "2")
    @ExcelProperty("是否同步供应商报价")
    @CompareField
    private Integer syncQuoteFlag;

    @Schema(description = "仓库id列表")
    @ExcelProperty("仓库id列表")
    @CompareField
    private String wmsIds;

    @Schema(description = "仓库名称列表")
    @ExcelProperty("仓库名称列表")
    @CompareField
    private String wmsNames;

    @Schema(description = "图片")
    @ExcelProperty("图片")
    @CompareField
    private SimpleFile mainPicture;

    @Schema(description = "缩略图")
    @ExcelProperty("缩略图")
    @CompareField
    private String thumbnail;

    @Schema(description = "产品类型")
    @ExcelProperty("产品类型")
    @CompareField
    private Integer skuType;

    @Schema(description = "均摊运费", example = "22972")
    @ExcelProperty(value = "均摊运费", converter = AmountConvert.class)
    private JsonAmount equallyFreight;

    @Schema(description = "均摊其他费用", example = "22972")
    @ExcelProperty(value = "均摊其他费用", converter = AmountConvert.class)
    private JsonAmount equallyOtherCost;

    @Schema(description = "交期", example = "100")
    @ExcelProperty(value = "交期")
    private Integer delivery;

    @Schema(description = "采购链接")
    @ExcelProperty(value = "采购链接")
    private String purchaseUrl;

    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    @Schema(description = "是否通用辅料")
    @ExcelProperty(value = "是否通用辅料")
    private Integer auxiliarySkuFlag;

    @Schema(description = "辅料采购类型")
    @ExcelProperty(value = "辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "规格描述")
    @ExcelProperty(value = "规格描述")
    private String specRemark;

    @Schema(description = "附件")
    @ExcelProperty(value = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "辅料属于的销售合同")
    @ExcelProperty(value = "辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    @Schema(description = "辅料属于的采购合同编号")
    @ExcelProperty(value = "辅料属于的采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的采购合同明细编号")
    @ExcelProperty(value = "辅料属于的采购合同明细编号")
    private Long auxiliaryPurchaseContractItemId;

    @Schema(description = "辅料属于的采购合同产品id")
    @ExcelProperty(value = "辅料属于的采购合同产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料属于的采购合同产品编号")
    @ExcelProperty(value = "辅料属于的采购合同产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料属于的采购合同产品名称")
    @ExcelProperty(value = "辅料属于的采购合同产品名称")
    private String auxiliarySkuName;

    @Schema(description = "辅料属于的采购合同产品客户货号")
    @ExcelProperty(value = "辅料属于的采购合同产品客户货号")
    private String auxiliaryCskuCode;

    @Schema(description = "验货状态")
    @ExcelProperty(value = "验货状态")
    private Integer checkType;

    @Schema(description = "验货费用")
    @ExcelProperty(value = "验货费用")
    private JsonAmount checkCost;

    @Schema(description = "计量单位")
    @ExcelProperty("计量单位")
    private String skuUnit;

    @Schema(description = "计量单位")
    private Integer measureUnit;

    @Schema(description = "自营产品标记")
    @ExcelProperty("自营产品标记")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标记")
    @ExcelProperty("客户产品标记")
    private Integer custProFlag;

    private Integer changeFlag;

    private String fileUrl;

    @Schema(description = "包装费用")
    @ExcelProperty(value = "包装费用", converter = AmountConvert.class)
    private JsonAmount packageCost;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "开票状态")
    private Integer invoiceStatus;

    @Schema(description = "已开票数量")
    private Integer invoicedQuantity;

    @Schema(description = "开票币种")
    private String invoicedCurrency;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;

    @Schema(description = "已支付金额")
    private BigDecimal paymentAmount;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "唯一编号")
    private String uniqueCode;

    @Schema(description = "来源编号")
    private String sourceUniqueCode;

    @Schema(description = "已转入库通知单数量")
    private Integer noticedQuantity;

    @Schema(description = "下推辅料合同标记")
    private Integer pushDownAuxiliaryFlag;

    @Schema(description = "登票状态")
    private Integer registerNoticeStatus;

    @Schema(description = "已登票状态")
    private Integer registerNoticeQuantity;

    @Schema(description = "入库状态")
    private Integer billStatus;

    @Schema(description = "入库数量")
    private Integer billQuantity;

    @Schema(description = "销售明细编号")
    private String saleItemUniqueCode;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @CompareField(value = "中文描述")
    private String description;

    @Schema(description = "关联销售合同明细")
    public SaleContractItemSaveDTO saleContractItemSaveDTO;

    @Schema(description = "销售合同币种")
    private String currencySaleContract;

    @Schema(description = "处理标识")
    private Integer handleFlag;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "是否分箱")
    private Integer splitBoxFlag;
    
    @Schema(description = "包材采购计划合同数量", example = "13785")
    private Integer totalQuantity;

    @Schema(description = "赠品数量")
    private Integer freeQuantity;

    @Schema(description = "是否拆分采购")
    private Integer splitFlag;

    @Schema(description = "客户po号")
    private String custPo;

    @Schema(description = "验货时间")
    private LocalDateTime inspectionTime;

    @ExcelProperty("自营产品货号")
    private String oskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;

    @Schema(description = "含税总价（人民币）", example = "22972")
    @ExcelProperty(value = "含税总价（人民币）", converter = AmountConvert.class)
    private JsonAmount totalPriceRmb;

}
