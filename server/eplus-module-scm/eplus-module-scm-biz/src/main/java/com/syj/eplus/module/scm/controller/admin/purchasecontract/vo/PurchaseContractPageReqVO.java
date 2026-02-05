package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购合同分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseContractPageReqVO extends PageParam {

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "采购合同编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "翻单标记", example = "1")
    private Integer repeatFlag;

    @Schema(description = "采购合同状态", example = "1")
    private Integer contractStatus;

    @Schema(description = "id数组")
    private Long[] idList;

    @Schema(description = "采购合同状态数组")
    private Integer[] contractStatusList;

    @Schema(description = "采购总金额")
    private JsonAmount totalAmount;

    @Schema(description = "采购总数量  采购数量之和")
    private Integer totalQuantity;

    @Schema(description = "打印状态  0-未打印、1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数 默认为 0")
    private Integer printTimes;

    @Schema(description = "预付款状态：1-无需预付、2-未预付、3-已预付", example = "1")
    private Integer prepayStatus;

    @Schema(description = "预付金额")
    private JsonAmount prepayAmount;

    @Schema(description = "付款状态  1-未付款、2-部分付款、3-完全付款", example = "2")
    private Integer payStatus;

    @Schema(description = "{金额，币种}、已付款金额（不含预付款）")
    private JsonAmount payedAmount;

    @Schema(description = "开票状态 1-未开票、2-部分开票、3-完全开票", example = "1")
    private Integer invoiceStatus;

    @Schema(description = "已开票金额 {金额，币种}")
    private BigDecimal invoicedAmount;

    @Schema(description = "跟单员Id", example = "王五")
    private Long managerId;

    @Schema(description = "业务员ID", example = "27774")
    private Long salesUserId;

    @Schema(description = "采购员编码", example = "27774")
    private Long purchaseUserId;

    @Schema(description = "采购员名称", example = "李四")
    private String purchaseUserName;

    @Schema(description = "采购员部门编码", example = "4724")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称", example = "李四")
    private String purchaseUserDeptName;

    @Schema(description = "客户主键", example = "29289")
    private Long custId;

    @Schema(description = "供应商主键", example = "1768")
    private Long venderId;

    @Schema(description = "仓库主键", example = "15661")
    private Long stockId;

    @Schema(description = "仓库编码")
    private String stockCode;

    @Schema(description = "供应商")
    private String vender;

    @Schema(description = "供应商编码")
    private String venderCode;

    @Schema(description = "供应商编码")
    private String venderName;

    @Schema(description = "供应商编码列表")
    private List<String> venderCodeList;

    @Schema(description = "客户编码")
    private String custCode;


    @Schema(description = "客户编码列表")
    private List<String> custCodeList;

    @Schema(description = "仓库名称", example = "芋艿")
    private String stockName;

    @Schema(description = "采购计划id", example = "11749")
    private Long purchasePlanId;

    @Schema(description = "采购计划编号")
    private String purchasePlanCode;

    @Schema(description = "销售合同id", example = "26936")
    private Long saleContractId;

    @Schema(description = "采购合同编号")
    private String saleContractCode;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "采购主体", example = "10492")
    private Long companyId;

    @Schema(description = "采购时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] purchaseTime;

    @Schema(description = "付款方式id", example = "18194")
    private Long PaymentId;

    @Schema(description = "付款方式名称", example = "全部付款")
    private String PaymentName;


    @Schema(description = "目的口岸", example = "22430")
    private Long portId;

    @Schema(description = "运费")
    private JsonAmount freight;

    @Schema(description = "其他费用")
    private JsonAmount otherCost;

    @Schema(description = "交货日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] deliveryDate;

    @Schema(description = "回签标识")
    private Integer signBackFlag;

    @Schema(description = "发票类型")
    private Integer taxType;

    @Schema(description = "分摊方式")
    private Integer equallyType;

    @Schema(description = "是否辅料采购")
    private Integer auxiliaryFlag;

    @Schema(description = "是否包含删除数据")
    private List<Boolean> deleteFlag = List.of(false);


    @Schema(description = "登票状态")
    private Integer registerNoticeStatus;


    @Schema(description = "自动生成")
    private Integer autoFlag;

    @Schema(description = "状态")
    private Integer neStatus;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "客户id")
    private List<Long> custIdList;

    @Schema(description = "付款方式")
    private Long paymentId;

    @Schema(description = "箱带颜色")
    private Integer boxWithColor;

    @Schema(description = "样品套数")
    private Integer sampleCount;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "含税单价最小值")
    private BigDecimal withTaxPriceMin;

    @Schema(description = "含税单价最大值")
    private BigDecimal withTaxPriceMax;

    @Schema(description = "人民币单价最小值")
    private BigDecimal withTaxPriceRmbMin;

    @Schema(description = "人民币单价最大值")
    private BigDecimal withTaxPriceRmbMax;

    @Schema(description = "合同总金额最小值")
    private BigDecimal totalAmountRangeMin;

    @Schema(description = "合同总金额最大值")
    private BigDecimal totalAmountRangeMax;

    @Schema(description = "合同总金额人民币最小值")
    private BigDecimal totalAmountRmbRangeMin;

    @Schema(description = "合同总金额人民币最大值")
    private BigDecimal totalAmountRmbRangeMax;

    @Schema(description = "辅料属于的采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    private String excludePurchaseContract;

    private String purchaseContractCode;


    private Integer exportFlag;

    @Schema(description = "是否优势产品")
    private Integer advantageFlag;

    @Schema(description = "优势产品SKU ID列表（内部使用，由advantageFlag查询后设置）")
    private List<Long> advantageSkuIds;

    @Schema(description = "优势产品SKU Code列表（内部使用，由advantageFlag查询后设置）")
    private List<String> advantageSkuCodes;

    @Schema(description = "查询模式：1-单据模式(按合同分页)，2-产品模式(按明细分页)", example = "1")
    private Integer queryMode;
}