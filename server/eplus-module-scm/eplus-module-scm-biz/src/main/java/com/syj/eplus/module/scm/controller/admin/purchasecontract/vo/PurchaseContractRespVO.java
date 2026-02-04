package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.LocalDateOnlyConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购合同 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class PurchaseContractRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    @ExcelProperty("主键")
    @ExcelIgnore
    private Long id;

    @Schema(description = "版本")
    @ExcelProperty("版本")
    @ExcelIgnore
    private Integer ver;

    @Schema(description = "确认状态")
    @ExcelProperty("确认状态")
    @ExcelIgnore
    private Integer confirmFlag;

    @Schema(description = "翻单标记")
    @ExcelProperty("翻单标记")
    private Integer repeatFlag;

    @Schema(description = "采购合同编号")
    @ExcelProperty(value = "采购合同编号")
    private String code;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "采购合同状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "采购合同编号", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.PURCHASE_CONTRACT_STATUS)
    private Integer contractStatus;

    /**
     * 生产完成标识:0-否 1-是
     */
    private Integer produceCompleted;

    /**
     * 转入库通知单标识
     */
    private Integer convertNoticeFlag;

    @Schema(description = "采购总金额")
    @ExcelProperty(value = "采购合同金额", converter = AmountConvert.class)
    @CompareField
    private JsonAmount totalAmount;

    @Schema(description = "采购总数量")
    @ExcelProperty("采购总数量")
    @CompareField
    private Integer totalQuantity;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印状态")
    @ExcelIgnore
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "预付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("预付款状态")
    @CompareField
    @ExcelIgnore
    private Integer prepayStatus;

    @Schema(description = "预付金额")
    @ExcelIgnore
    @CompareField
    private JsonAmount prepayAmount;

    @ExcelProperty(value = "预付金额")
    private BigDecimal prepayAmountAmount;

    @ExcelProperty(value = "预付金额币种")
    private String prepayAmountCurrency;

    @Schema(description = "付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("付款状态")
    @CompareField
    @ExcelIgnore
    private Integer payStatus;

    @Schema(description = "已付款金额（不含预付款）")
    @ExcelIgnore
    @CompareField
    private JsonAmount payedAmount;

    @ExcelProperty(value = "已付款金额（不含预付款）")
    private BigDecimal payedAmountAmount;

    @ExcelProperty(value = "已付款金额币种")
    private String payedAmountCurrency;

    @Schema(description = "开票状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("开票状态")
    @CompareField
    @ExcelIgnore
    private Integer invoiceStatus;

    @Schema(description = "已开票金额")
    @ExcelProperty(value = "已开票金额")
    @CompareField
    private BigDecimal invoicedAmount;

    @Schema(description = "跟单员", example = "24538")
    @ExcelProperty("跟单员")
    @CompareField
    private UserDept manager;

    @Schema(description = "跟单员姓名")
    @ExcelProperty("跟单员姓名")
    private String managerNickName;

    @Schema(description = "采购员编码", example = "27774")
    @ExcelProperty("采购员编码")
    @CompareField
    @ExcelIgnore
    private Long purchaseUserId;

    @Schema(description = "采购员名称", example = "李四")
    @ExcelProperty("采购员名称")
    @CompareField
    private String purchaseUserName;

    @Schema(description = "采购员部门编码", example = "4724")
    @ExcelProperty("采购员部门编码")
    @CompareField
    @ExcelIgnore
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称", example = "李四")
    @ExcelProperty("采购员部门名称")
    @CompareField
    private String purchaseUserDeptName;

    @Schema(description = "客户主键", example = "29289")
    @ExcelProperty("客户主键")
    @CompareField
    @ExcelIgnore
    private Long custId;

    @Schema(description = "供应商主键", example = "1768")
    @ExcelProperty("供应商主键")
    @CompareField
    @ExcelIgnore
    private Long venderId;

    @Schema(description = "仓库主键", example = "15661")
    @ExcelProperty("仓库主键")
    @CompareField
    @ExcelIgnore
    private Long stockId;

    @Schema(description = "仓库编码")
    @ExcelProperty("仓库编码")
    @CompareField
    private String stockCode;

    @Schema(description = "供应商编码")
    @ExcelProperty("供应商编码")
    @CompareField
    private String venderCode;

    @Schema(description = "供应商名称")
    @ExcelProperty("供应商编码")
    @CompareField
    private String venderName;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @CompareField
    private String currency;

    @Schema(description = "客户编码")
    @ExcelProperty("客户编码")
    @CompareField
    private String custCode;

    @Schema(description = "客户名称")
    @ExcelProperty("客户名称")
    @CompareField
    private String custName;


    @Schema(description = "仓库名称", example = "芋艿")
    @ExcelProperty("仓库名称")
    @CompareField
    private String stockName;

    @Schema(description = "采购计划id", example = "11749")
    @ExcelProperty("采购计划id")
    @CompareField
    @ExcelIgnore
    private Long purchasePlanId;

    @Schema(description = "采购计划编号")
    @ExcelProperty("采购计划编号")
    @CompareField
    private String purchasePlanCode;

    @Schema(description = "销售合同id", example = "26936")
    @ExcelProperty("销售合同id")
    @CompareField
    @ExcelIgnore
    private Long saleContractId;

    @Schema(description = "采购合同编号")
    @ExcelProperty("采购合同编号")
    @CompareField
    private String saleContractCode;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    @CompareField
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @CompareField
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    @CompareField
    @ExcelIgnore
    private String creator;

    @Schema(description = "创建人姓名")
    @ExcelProperty("创建人姓名")
    @CompareField
    private String creatorName;

    @Schema(description = "创建人部门")
    @ExcelProperty("创建人部门")
    @ExcelIgnore
    private String creatorDeptName;

    @Schema(description = "附件")
    @ExcelProperty(value = "附件")
    @ExcelIgnore
    @CompareField
    private List<SimpleFile> annex;

    @Schema(description = "采购主体", example = "10492")
    @ExcelProperty("采购主体")
    @CompareField
    @ExcelIgnore
    private Long companyId;

    @Schema(description = "采购主体名称", example = "泛太机电")
    @ExcelProperty("采购主体名称")
    private String companyName;

    @Schema(description = "采购时间")
    @ExcelProperty("采购时间")
    @CompareField
    private LocalDateTime purchaseTime;


    @Schema(description = "付款方式id", example = "18194")
    @CompareField
    @ExcelIgnore
    private Long paymentId;

    @Schema(description = "付款方式", example = "18194")
    @ExcelProperty("付款方式")
    @CompareField
    private String paymentName;

    @Schema(description = "目的口岸id", example = "22430")
    @CompareField
    @ExcelIgnore
    private Long portId;

    @Schema(description = "目的口岸", example = "22430")
    @ExcelProperty("目的口岸")
    @CompareField
    private String portName;

    @Schema(description = "运费")
    @ExcelIgnore
    @CompareField
    private JsonAmount freight;

    @ExcelProperty(value = "运费")
    private BigDecimal freightAmount;

    @ExcelProperty(value = "运费币种")
    private String freightCurrency;

    @Schema(description = "其他费用")
    @ExcelIgnore
    @CompareField
    private JsonAmount otherCost;

    @ExcelProperty(value = "其他费用")
    private BigDecimal otherCostAmount;

    @ExcelProperty(value = "其他费用币种")
    private String otherCostCurrency;

    @Schema(description = "交货日期")
    @ExcelProperty(value = "交货日期", converter = LocalDateOnlyConvert.class)
    @CompareField
    private LocalDateTime deliveryDate;
    
    @Schema(description = "初始交货日期")
    @CompareField
    private LocalDateTime initDeliveryDate;

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
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.TAX_TYPE)
    @CompareField
    private Integer taxType;

    @Schema(description = "分摊方式")
    @ExcelProperty(value = "分摊方式", converter = DictConvert.class)
    @DictFormat("equally_type")
    @CompareField
    private Integer equallyType;

    @Schema(description = "重构标记")
    @ExcelProperty(value = "重构标记")
    private Integer rePurchaseFlag;

    @Schema(description = "重构说明")
    @ExcelProperty(value = "重构说明")
    private String rePurchaseDesc;

    @Schema(description = "重构时间")
    @ExcelProperty(value = "重构时间")
    private String rePurchaseTime;

    @Schema(description = "重构旧版本标记")
    @ExcelProperty(value = "重构旧版本标记")
    private Integer rePurchaseOldFlag;

    @Schema(description = "是否辅料采购")
    @ExcelProperty(value = "是否辅料采购")
    private Integer auxiliaryFlag;

    @Schema(description = "应付供应商主键")
    private Long paymentVenderId;

    @Schema(description = "应付供应商编号")
    private String paymentVenderCode;

    @Schema(description = "应付供应商名称")
    private String paymentVenderName;

    @Schema(description = "已开票数量")
    private BigDecimal invoicedQuantity;

    @Schema(description = "登票币种")
    private String invoicedCurrency;

    @Schema(description = "回签时间")
    private LocalDateTime signBackTime;

    @Schema(description = "回签描述")
    private String signBackDesc;

    @Schema(description = "变更状态 1-未变更 2-变更中 3-已变更")
    private Integer changeStatus;

    @Schema(description = "自动生成标识 0-否 1-是")
    private Integer autoFlag;

    @Schema(description = "出片文件")
    private List<SimpleFile> designDraftList;

    @Schema(description = "回签附件")
    private List<SimpleFile> signBackAnnexList;

    @Schema(description = "下单标记")
    private Integer placeOrderFlag;

    @Schema(description = "下单时间")
    private LocalDateTime placeOrderTime;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "采购计划来源方式")
    private Integer planSourceType;

    @Schema(description = "登票状态")
    @ExcelProperty(value = "登票状态")
    private Integer registerNoticeStatus;

    /**
     * 验货状态：0-未验货、1-部分通过、2-全部通过
     */
    private Integer checkStatus;

    @Schema(description = "箱带颜色")
    @ExcelProperty(value = "箱带颜色")
    private Integer boxWithColor;

    @Schema(description = "样品套数")
    @ExcelProperty(value = "样品套数")
    private Integer sampleCount;

    @Schema(description = "业务员")
    @ExcelProperty(value = "业务员",converter = UserDeptConverter.class)
    private UserDept sales;

    @Schema(description = "送货地址")
    private String deliveryAddress;
    @Schema(description = "包材下推对公标记")
    private Integer auxiliaryPaymentFlag;
    @Schema(description = "费用归集")
    private List<FeeShareRespDTO> feeShare;

    /**
     * 最低备品比例
     */
    private BigDecimal minimumBaseQuantity;

    /**
     * 乙方补货时限
     */
    private Integer restockingDeadline;

    /**
     * 应付金额
     */
    private JsonAmount paymentAmount;

    /**
     * 剩余未付金额
     */
    private JsonAmount unPaidAmount;

    @Schema(description = "辅料属于的采购员")
    private List<UserDept> auxiliaryPurchaseUser;

    @Schema(description = "辅料属于的销售员")
    private List<UserDept> auxiliarySales;

    @Schema(description = "辅料属于的跟单员")
    private List<UserDept> auxiliaryManager;

    @Schema(description = "采购总额人民币")
    private JsonAmount totalAmountRmb;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "采购合同金额RMB")
    @ExcelProperty(value = "采购合同金额RMB")
    private BigDecimal totalAmountRmbAmount;
}
