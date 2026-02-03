package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 公对公申请新增/修改 Request VO")
@Data
public class PaymentAppSaveReqVO extends ReimbSaveReqVO {

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印状态不能为空")
    private Integer printFlag;

    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "打印次数不能为空")
    private Integer printTimes;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "不对")
//    @NotEmpty(message = "事由")
    private String reason;

    @Schema(description = "内部法人单位", example = "30861")
    private Long companyId;

    @Schema(description = "支付对象")
    private Integer businessSubjectType;

    @Schema(description = "业务编号")
    private String businessSubjectCode;

    @Schema(description = "业务名称")
    private String businessSubjectName;

    @Schema(description = "申请人")
    private UserDept applyer;

    @Schema(description = "支付金额")
    private JsonAmount amount;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行地址")
    private String bankAddress;

    @Schema(description = "银行账号", example = "5476")
    private String bankAccount;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "已支付金额")
    private JsonAmount paymentAmount;

    @Schema(description = "已支付币种")
    private String paymentCurrency;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付日期")
    private LocalDateTime paymentDate;

    @Schema(description = "关联类型")
    private Integer relationType;

    @Schema(description = "关联编号")
    private List<String> relationCode;

    @Schema(description = "费用归属类型")
    private Integer auxiliaryType;

    @Schema(description = "是否费用归属类型")
    private Integer feeShareFlag;

    @Schema(description = "是否预付")
    private Integer prepaidFlag;

    private Integer submitFlag;

    @Schema(description = "预付申请单编号列表")
    private List<String> paymentAppList;

    @Schema(description = "发票金额")
    private JsonAmount invoiceAmount;

    @Schema(description = "发票附件")
    private List<SimpleFile> annex;

    @Schema(description = "累计支付金额")
    private JsonAmount totalPaymentAmount;

    @Schema(description = "累计发票金额")
    private JsonAmount totalInvoiceAmount;

    @Schema(description = "发票标识")
    private Integer invoiceFlag;

    @Schema(description = "支付金额")
    private JsonAmount payAmount;

    @Schema(description = "备注")
    private String remark;
}