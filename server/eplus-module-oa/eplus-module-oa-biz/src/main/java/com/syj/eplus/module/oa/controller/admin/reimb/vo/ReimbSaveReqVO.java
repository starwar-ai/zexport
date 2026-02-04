package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出差报销新增/修改 Request VO")
@Data
public class ReimbSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11016")
    private Long id;

    @Schema(description = "表单编码")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "报销类型", example = "1")
    private Integer reimbType;

    @Schema(description = "费用说明", example = "随便")
    private String description;

    @Schema(description = "报销主体", example = "10054")
    private Long companyId;

    @Schema(description = "报销人", example = "13687")
    private UserDept reimbUser;

    @Schema(description = "报销明细")
    private List<JsonReimbDetail> reimbDetailList;

    @Schema(description = "关联合同列表")
    private String contractList;

    @Schema(description = "报销金额列表")
    private List<JsonAmount> amountList;

    @Schema(description = "发票总金额列表")
    private List<JsonAmount> invoiceAmountList;

    @Schema(description = "本次报销金额列表")
    private List<JsonAmount> totalAmountList;

    @Schema(description = "是否还款")
    private Integer repayFlag;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "费用归属对象")
    private List<FeeShareReqDTO> feeShare;

    @Schema(description = "费用归属对象类型")
    private Integer auxiliaryType;

    @Schema(description = "实际使用人")
    private UserDept actualUser;

    @Schema(description = "出纳员")
    private UserDept cashier;

    @Schema(description = "发票")
    private List<SimpleFile> invoice;

    @Schema(description = "费用归属状态")
    private Integer auxiliaryStatus;

    @Schema(description = "费用用途（正式）")
    private String expenseUseToFormal;

    @Schema(description = "费用用途（发生）")
    private String expenseUseToOccur;

    @Schema(description = "科目主键")
    private Long financialSubjectId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "类别配置主键")
    private Long dictSubjectId;

    @Schema(description = "费用用途主键")
    private Long expenseUseToFormalId;

    /**
     * 借款申请单列表
     */
    private List<SimpleLoanAppRespDTO> loanAppList;

    @Schema(description = "发票价主键")
    private Long invoiceHolderId;

    @Schema(description = "实际使用人id")
    private Long actualUserId;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "费用申请id列表")
    private List<Long> applyIdList;

    @Schema(description = "还款金额列表")
    private List<JsonAmount> repayAmountList;

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "作废原因")
    private String cancelReason;

    @Schema(description = "作废人")
    private UserDept cancelUser;
}