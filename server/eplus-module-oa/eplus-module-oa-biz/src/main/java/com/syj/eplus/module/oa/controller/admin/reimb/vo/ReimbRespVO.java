package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.AmountListConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 报销 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReimbRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11016")
    private Long id;

    @Schema(description = "表单编码")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "费用说明", example = "随便")
    @ExcelProperty("费用说明")
    private String description;

    @Schema(description = "报销主体id", example = "10054")
    private Long companyId;

    @Schema(description = "报销主体名称", example = "10054")
    @ExcelProperty("报销主体")
    private String companyName;

    @Schema(description = "报销人")
    @ExcelProperty(value = "报销人", converter = UserDeptConverter.class)
    private UserDept reimbUser;

    @Schema(description = "报销明细")
    private List<JsonReimbDetail> reimbDetailList;

    @Schema(description = "关联订单id")
    private Long contractId;

    @Schema(description = "报销金额列表")
    @ExcelProperty(value = "报销金额", converter = AmountListConvert.class)
    private List<JsonAmount> amountList;

    @Schema(description = "发票总金额列表")
    @ExcelProperty(value = "发票总金额", converter = AmountListConvert.class)
    private List<JsonAmount> invoiceAmountList;

    @Schema(description = "本次报销金额列表")
    @ExcelProperty(value = "本次报销金额", converter = AmountListConvert.class)
    private List<JsonAmount> totalAmountList;

    @Schema(description = "支付状态", example = "1")
    @ExcelProperty(value = "支付状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer paymentStatus;

    @Schema(description = "支付金额")
    @ExcelProperty(value = "支付金额", converter = AmountConvert.class)
    private JsonAmount paymentAmount;

    @Schema(description = "支付日期")
    @ExcelProperty("支付日期")
    private LocalDateTime paymentDate;

    @Schema(description = "是否还款")
    @ExcelProperty(value = "是否还款", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.IS_INT)
    private Integer repayFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行账号", example = "5580")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行地址")
    @ExcelProperty("开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "费用归属对象列表")
    private List<ReimbShareRespVO> auxiliaryList;

    @Schema(description = "费用归属对象类型")
    private Integer auxiliaryType;

    @Schema(description = "实际操作人")
    private Integer userFlag;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "报销类型")
    private Integer reimbType;

    @Schema(description = "实际使用人")
    private UserDept actualUser;

    @Schema(description = "出纳员")
    @ExcelProperty(value = "出纳员", converter = UserDeptConverter.class)
    private UserDept cashier;

    @Schema(description = "发票")
    private List<SimpleFile> invoice;

    @Schema(description = "费用用途（正式）")
    private String expenseUseToFormal;

    @Schema(description = "费用用途（发生）")
    private String expenseUseToOccur;

    @Schema(description = "科目主键")
    private Long financialSubjectId;

    @Schema(description = "科目名称")
    private String financialSubjectName;

    @Schema(description = "费用归集")
    private List<FeeShareRespDTO> feeShare;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "类别配置主键")
    private Long dictSubjectId;

    @Schema(description = "费用用途主键")
    private Long expenseUseToFormalId;

    @Schema(description = "费用名称")
    private String feeName;

    /**
     * 借款申请单列表
     */
    private List<SimpleLoanAppRespDTO> loanAppList;

    @Schema(description = "发票价主键")
    private Long invoiceHolderId;

    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "打印日期")
    private LocalDateTime printDate;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "费用申请id列表")
    private List<Long> applyIdList;

    @Schema(description = "当前审批人")
    private UserDept approveUser;

    @Schema(description = "借款单还款金额列表")
    private List<JsonAmount> repayAmountList;


    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "作废原因")
    private String cancelReason;

    @Schema(description = "作废人")
    private UserDept cancelUser;

    @Schema(description = "创建人")
    private String creator;
}