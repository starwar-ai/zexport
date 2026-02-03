package com.syj.eplus.module.scm.entity;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonEffectRangeTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.handler.JsonPurchaseContractItemHandler;
import com.syj.eplus.module.scm.handler.JsonPurchaseContractRespHandler;
import com.syj.eplus.module.scm.handler.JsonPurchasePaymentPlanHandler;
import com.syj.eplus.module.scm.handler.JsonPurchaseSubAddTermHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 17:48
 */
@TableName(value = "scm_purchase_contract_change", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class PurchaseContractChange extends BaseDO implements ModelKeyHolder {
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "变更单编号")
    private String code;

    @Schema(description = "采购合同编号")
    private String contractCode;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer auditStatus;

    @Schema(description = "采购合同状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer contractStatus;

    @Schema(description = "采购总金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;


    @Schema(description = "采购总数量")
    private Integer totalQuantity;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "预付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer prepayStatus;

    @Schema(description = "预付金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount prepayAmount;

    @Schema(description = "付款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer payStatus;

    @Schema(description = "已付款金额（不含预付款）")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount payedAmount;

    @Schema(description = "开票状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer invoiceStatus;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;

    @CompareField(value = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    @Schema(description = "采购员编码", example = "27774")
    private Long purchaseUserId;

    @Schema(description = "采购员名称", example = "李四")
    @CompareField(value = "采购员")
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

    @Schema(description = "供应商名称")
    private String venderName;

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
    @CompareField(value = "备注")
    private String remark;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "附件")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    @Schema(description = "采购主体", example = "10492")
    private Long companyId;

    @Schema(description = "采购主体", example = "10492")
    private String companyName;

    @Schema(description = "采购时间")
    private LocalDateTime purchaseTime;

    @Schema(description = "付款方式id", example = "18194")
    private Long paymentId;

    @Schema(description = "是否同步供应商")
    private Integer syncQuoteFlag;

    @Schema(description = "是否赠品")
    private Integer freeFlag;

    @Schema(description = "目的口岸", example = "22430")
    private Long portId;

    @Schema(description = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "运费")
    private JsonAmount freight;

    @Schema(description = "其他费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "其他费用")
    private JsonAmount otherCost;

    @Schema(description = "交货时间")
    private LocalDateTime deliveryDate;

    @Schema(description = "计划到料时间")
    @CompareField(value = "预计交期")
    private LocalDateTime plannedArrivalTime;

    @Schema(description = "采购产品总数量")
    private Integer itemCountTotal;

    @Schema(description = "采购计划明细列表")
    @TableField(typeHandler = JsonPurchaseContractItemHandler.class)
    private List<PurchaseContractItemInfoRespVO> children;

    private String processInstanceId;


    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept createUser;

    @Schema(description = "回签标识")
    private Integer signBackFlag;

    @Schema(description = "发票类型")
    @CompareField(value = "发票类型", enumType = "tax_type")
    private Integer taxType;

    @Schema(description = "分摊方式")
    @CompareField(value = "分摊方式", enumType = "equally_type")
    private Integer equallyType;

    @Schema(description = "口岸名称")
    @CompareField(value = "目的口岸")
    private String portName;

    @Schema(description = "付款方式", example = "18194")
    @ExcelProperty("付款方式")
    @CompareField(value = "付款方式")
    private String paymentName;

    @Schema(description = "客户名称")
    @CompareField(value = "客户名称")
    private String custName;

    @Schema(description = "付款计划")
    @TableField(typeHandler = JsonPurchasePaymentPlanHandler.class)
    private List<PurchasePaymentPlan> purchasePaymentPlanList;

    @Schema(description = "加减项")
    @TableField(typeHandler = JsonPurchaseSubAddTermHandler.class)
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

    @Schema(description = "操作日志")
    @TableField(exist = false)
    private List<OperateLogRespDTO> operateLogRespDTOList;

    private String modelKey;


    @TableField(typeHandler = JsonPurchaseContractRespHandler.class)
    private PurchaseContractInfoRespVO oldData;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    private String sourceCode;

    /**
     * 出片文件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> designDraftList;

    @Schema(description = "包材标记")
    private Integer auxiliaryFlag;

    /**
     * 箱带颜色
     */
    @CompareField(value = "箱带颜色")
    private Integer boxWithColor;
    /**
     * 样品套数
     */
    @CompareField(value = "样品套数")
    private Integer sampleCount;

    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    /**
     * 回签时间
     */
    private LocalDateTime signBackTime;

    /**
     * 币种
     */
    private String currency;

    /**
     * 税率
     */
    private BigDecimal taxRate;
}
