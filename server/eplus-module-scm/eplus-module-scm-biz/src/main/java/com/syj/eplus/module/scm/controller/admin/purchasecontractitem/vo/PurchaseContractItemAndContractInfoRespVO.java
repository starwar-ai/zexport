package com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo;

import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
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
public class PurchaseContractItemAndContractInfoRespVO extends  PurchaseContractItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11767")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "版本")
    @ExcelProperty("版本")
    private Integer ver;

    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;

    @Schema(description = "skuid", example = "31593")
    @ExcelProperty("skuid")
    private Long skuId;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    private String skuName;

    @Schema(description = "客户id", example = "18970")
    @ExcelProperty("客户ID")
    private Long custId;

    @Schema(description = "客户编号")
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "采购数量")
    @ExcelProperty("采购数量")
    private Integer quantity;

    @Schema(description = "采购含税金额")
    @ExcelProperty(value = "采购含税金额", converter = AmountConvert.class)
    private JsonAmount withTaxTotal;

    @Schema(description = "供应商id", example = "27269")
    @ExcelProperty("供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    @ExcelProperty("供应商编号")
    private String venderCode;

    @Schema(description = "计划到料日期")
    @ExcelProperty("计划到料日期")
    private LocalDateTime planArriveDate;

    @Schema(description = "验货状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("验货状态")
    private Integer checkStatus;

    @Schema(description = "已验货数量")
    @ExcelProperty("已验货数量")
    private Integer checkedQuantity;

    @Schema(description = "验收状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("验收状态")
    private Integer receiveStatus;

    @Schema(description = "已收货数量")
    @ExcelProperty("已收货数量")
    private Integer receivedQuantity;

    @Schema(description = "退货量")
    @ExcelProperty("退货量")
    private Integer exchangeQuantity;

    @Schema(description = "换货量")
    @ExcelProperty("换货量")
    private Integer returnQuantity;

    @Schema(description = "采购合同单号", example = "15766")
    @ExcelProperty("采购合同单号")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "内箱装量")
    @ExcelProperty("内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    @ExcelProperty("包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "包装规格宽度")
    @ExcelProperty("包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    @ExcelProperty("包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    @ExcelProperty("包装规格单位")
    private Integer packageUnit;

    @Schema(description = "外箱规格长度")
    @ExcelProperty("外箱规格长度")
    private BigDecimal outerboxLength;

    @Schema(description = "外箱规格宽度")
    @ExcelProperty("外箱规格宽度")
    private BigDecimal outerboxWidth;

    @Schema(description = "外箱规格高度")
    @ExcelProperty("外箱规格高度")
    private BigDecimal outerboxHeight;

    @Schema(description = "外箱规格单位")
    @ExcelProperty("外箱规格单位")
    private Integer outerboxUnit;

    @Schema(description = "外箱体积")
    @ExcelProperty("外箱体积")
    private BigDecimal outerboxVolume;

    @Schema(description = "外箱净重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("外箱净重")
    private JsonWeight outerboxNetweight;

    @Schema(description = "单品毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单品毛重")
    private JsonWeight singleGrossweight;

    @Schema(description = "外箱毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("外箱毛重")
    private JsonWeight outerboxGrossweight;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "包装价", example = "17301")
    @ExcelProperty(value = "包装价", converter = AmountConvert.class)
    private JsonAmount packagingPrice;

    @Schema(description = "运费", example = "17301")
    @ExcelProperty(value = "运费", converter = AmountConvert.class)
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价", example = "31058")
    @ExcelProperty(value = "采购单价", converter = AmountConvert.class)
    private JsonAmount unitPrice;

    @Schema(description = "总价", example = "4346")
    @ExcelProperty(value = "总价", converter = AmountConvert.class)
    private JsonAmount totalPrice;

    @Schema(description = "含税总价", example = "22972")
    @ExcelProperty(value = "含税总价", converter = AmountConvert.class)
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("采购类型")
    private Integer purchaseType;

    @Schema(description = "采购员id", example = "17679")
    @ExcelProperty("采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名", example = "张三")
    @ExcelProperty("采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id", example = "11166")
    @ExcelProperty("采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门不名称", example = "王五")
    @ExcelProperty("采购员部门不名称")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    @ExcelProperty("工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    @ExcelProperty("报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    @ExcelProperty("是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    @ExcelProperty("是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式", example = "1")
    @ExcelProperty("包装方式")
    private List<Long> packageType;

    @Schema(description = "包装方式中文名", example = "1")
    @ExcelProperty("包装方式中文名")
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    @ExcelProperty("包装方式英文名")
    private String packageTypeEngName;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currency;

    @Schema(description = "是否含包装")
    @ExcelProperty("是否含包装")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    @ExcelProperty("最小起购量")
    private Integer moq;

    @Schema(description = "箱数", example = "3726")
    @ExcelProperty("箱数")
    private Integer boxCount;

    @Schema(description = "入库状态", example = "2")
    @ExcelProperty("入库状态")
    private Integer warehousingType;

    @Schema(description = "仓库id列表")
    @ExcelProperty("仓库id列表")
    private String wmsIds;

    @Schema(description = "仓库名称列表")
    @ExcelProperty("仓库名称列表")
    private String wmsNames;

    @Schema(description = "图片")
    @ExcelProperty("图片")
    private SimpleFile mainPicture;

    @Schema(description = "产品类型")
    @ExcelProperty("产品类型")
    private Integer skuType;

    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    private String code;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "采购合同状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("采购合同状态")
    private Integer contractStatus;

    @Schema(description = "采购总金额")
    @ExcelProperty(value = "采购总金额", converter = AmountConvert.class)
    @CompareField
    private JsonAmount totalAmount;

    @Schema(description = "采购总数量")
    @ExcelProperty("采购总数量")
    @CompareField
    private Integer totalQuantity;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "预付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("预付款状态")
    @CompareField
    private Integer prepayStatus;

    @Schema(description = "预付金额")
    @ExcelProperty(value = "预付金额", converter = AmountConvert.class)
    @CompareField
    private JsonAmount prepayAmount;

    @Schema(description = "付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("付款状态")
    @CompareField
    private Integer payStatus;

    @Schema(description = "已付款金额（不含预付款）")
    @ExcelProperty(value = "已付款金额（不含预付款）", converter = AmountConvert.class)
    @CompareField
    private JsonAmount payedAmount;

    @Schema(description = "开票状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("开票状态")
    @CompareField
    private Integer invoiceStatus;

    @Schema(description = "跟单员", example = "24538")
    @ExcelProperty("跟单员")
    @CompareField
    private UserDept manager;

    @Schema(description = "仓库主键", example = "15661")
    @ExcelProperty("仓库主键")
    @CompareField
    private Long stockId;

    @Schema(description = "仓库编码")
    @ExcelProperty("仓库编码")
    @CompareField
    private String stockCode;


    @Schema(description = "供应商名称")
    @ExcelProperty("供应商编码")
    @CompareField
    private String venderName;


    @Schema(description = "仓库名称", example = "芋艿")
    @ExcelProperty("仓库名称")
    @CompareField
    private String stockName;

    @Schema(description = "采购计划id", example = "11749")
    @ExcelProperty("采购计划id")
    @CompareField
    private Long purchasePlanId;

    @Schema(description = "采购计划编号")
    @ExcelProperty("采购计划编号")
    @CompareField
    private String purchasePlanCode;

    @Schema(description = "销售合同id", example = "26936")
    @ExcelProperty("销售合同id")
    @CompareField
    private Long saleContractId;

    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    @CompareField
    private String saleContractCode;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    @CompareField
    private String remark;


    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    @CompareField
    private String creator;

    @Schema(description = "创建人姓名")
    @ExcelProperty("创建人姓名")
    @CompareField
    private String creatorName;


    @Schema(description = "采购主体", example = "10492")
    @ExcelProperty("采购主体")
    @CompareField
    private Long companyId;

    @Schema(description = "s", example = "泛太机电")
    @ExcelProperty("采购主体名称")
    private String companyName;

    @Schema(description = "采购时间")
    @ExcelProperty("采购时间")
    @CompareField
    private LocalDateTime purchaseTime;

    @Schema(description = "付款方式id", example = "18194")
    @ExcelProperty("付款方式id")
    @CompareField
    private Long paymentId;

    @Schema(description = "是否同步供应商")
    @ExcelProperty("是否同步供应商")
    @CompareField
    private Integer syncQuoteFlag;

    @Schema(description = "是否赠品")
    @ExcelProperty("是否赠品")
    @CompareField
    private Integer freeFlag;

    @Schema(description = "目的口岸", example = "22430")
    @ExcelProperty("目的口岸")
    @CompareField
    private Long portId;

    @Schema(description = "运费")
    @ExcelProperty(value = "运费", converter = AmountConvert.class)
    @CompareField
    private JsonAmount freight;

    @Schema(description = "其他费用")
    @ExcelProperty(value = "其他费用", converter = AmountConvert.class)
    @CompareField
    private JsonAmount otherCost;

    @Schema(description = "交货时间")
    @ExcelProperty("交货时间")
    @CompareField
    private LocalDateTime deliveryDate;


    @Schema(description = "采购产品总数量")
    @ExcelProperty("采购产品总数量")
    @ExcelIgnore
    @CompareField
    private Integer itemCountTotal;

    @Schema(description = "回签标识")
    @ExcelProperty("回签标识")
    @CompareField
    private Integer signBackFlag;

    @Schema(description = "发票类型")
    @ExcelProperty("发票类型")
    @CompareField
    private Integer taxType;


    @Schema(description = "交期")
    @ExcelProperty("交期")
    @CompareField
    private Integer delivery;

    @Schema(description = "采购链接")
    @ExcelProperty("采购链接")
    @CompareField
    private String purchaseUrl;

}