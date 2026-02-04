package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购合同新增/修改 Request VO")
@Data
public class PurchaseContractSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "采购合同编号")
    private String code;

    @Schema(description = "确认状态")
    private Integer confirmFlag;

    @Schema(description = "翻单标记")
    private Integer repeatFlag;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "采购合同状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "采购合同状态不能为空")
    private Integer contractStatus;

    @Schema(description = "采购总金额")
    private JsonAmount totalAmount;

    @Schema(description = "采购总数量")
    private Integer totalQuantity;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印状态不能为空")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "预付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "预付款状态不能为空")
    private Integer prepayStatus;

    @Schema(description = "预付金额")
    private JsonAmount prepayAmount;

    @Schema(description = "付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "付款状态不能为空")
    private Integer payStatus;

    @Schema(description = "已付款金额（不含预付款）")
    private JsonAmount payedAmount;

    @Schema(description = "开票状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "开票状态不能为空")
    private Integer invoiceStatus;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;

    @Schema(description = "跟单员")
    private UserDept manager;

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

    @Schema(description = "供应商编码")
    private String venderCode;

    @Schema(description = "客户编码")
    private String custCode;

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

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "采购主体", example = "10492")
    private Long companyId;

    @Schema(description = "采购时间")
    private LocalDateTime purchaseTime;

    @Schema(description = "付款方式id", example = "18194")
    private Long paymentId;

    @Schema(description = "付款方式名称", example = "18194")
    private String paymentName;


    @Schema(description = "目的口岸", example = "22430")
    private Long portId;

    @Schema(description = "运费")
    private JsonAmount freight;

    @Schema(description = "其他费用")
    private JsonAmount otherCost;

    @Schema(description = "交货日期")
    private LocalDateTime deliveryDate;


    @Schema(description = "回签标识")
    private Integer signBackFlag;


    @Schema(description = "发票类型")
    private Integer taxType;


    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "分摊方式")
    private Integer equallyType;


    @Schema(description = "采购模式")
    private Integer purchaseModel;

    @Schema(description = "是否辅料采购")
    private Integer auxiliaryFlag;

    @Schema(description = "加减项列表")
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

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

    private String currency;

    @Schema(description = "付款计划")
    private List<PurchasePaymentPlan> purchasePaymentPlanList;

    @Schema(description = "自动生成标识 0-否 1-是")
    private Integer autoFlag;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "出片文件")
    private List<SimpleFile> designDraftList;

    @Schema(description = "采购计划来源方式")
    private Integer planSourceType;

    @Schema(description = "业务员")
    private UserDept sales;
    @Schema(description = "箱带颜色")
    private Integer boxWithColor;

    @Schema(description = "样品套数")
    private Integer sampleCount;

    private Integer rollbackCodeToShipmentFlag;

    private String genContractUniqueCode;
    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 最低备品比例
     */
    private BigDecimal minimumBaseQuantity;

    /**
     * 乙方补货时限
     */
    private Integer restockingDeadline;

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
}
