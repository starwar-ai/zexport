package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 公对公申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentAppRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16083")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "申请单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请单号")
    private String code;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "打印状态", converter = DictConvert.class)
    @DictFormat("print_flag") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer printFlag;

    @Schema(description = "打印次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "不对")
    @ExcelProperty("事由")
    private String reason;

    @Schema(description = "内部法人单位id", example = "30861")
    private Long companyId;

    @Schema(description = "内部法人单位名称", example = "30861")
    @ExcelProperty("内部法人单位名称")
    private String companyName;

    @Schema(description = "业务类型")
    private Integer businessSubjectType;

    @Schema(description = "业务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("业务名称")
    private String businessSubjectName;

    @Schema(description = "业务编号")
    private String businessSubjectCode;

    @Schema(description = "申请人")
    @ExcelProperty(value = "申请人", converter = UserDeptConverter.class)
    private UserDept applyer;


    @Schema(description = "支付金额")
    @ExcelProperty(value = "支付金额", converter = AmountConvert.class)
    private JsonAmount amount;

    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行地址")
    @ExcelProperty("银行地址")
    private String bankAddress;

    @Schema(description = "银行账号", example = "5476")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "已支付金额")
    @ExcelProperty(value = "已支付金额", converter = AmountConvert.class)
    private JsonAmount paymentAmount;

    @Schema(description = "已支付币种")
    @ExcelProperty("已支付币种")
    private String paymentCurrency;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付日期")
    private LocalDateTime paymentDate;

    @Schema(description = "费用归属类型")
    private Integer auxiliaryType;

    @Schema(description = "费用归属对象列表")
    private List<FeeShareRespDTO> feeShare;

    @Schema(description = "是否费用归属类型")
    private Integer feeShareFlag;

    @Schema(description = "是否预付")
    private Integer prepaidFlag;

    @Schema(description = "发票金额")
    private List<JsonAmount> invoiceAmountList;

    @Schema(description = "发票")
    private List<SimpleFile> invoice;

    @Schema(description = "预付申请单列表")
    private List<SimplePaymentAppResp> paymentAppDTOList;

    @Schema(description = "预付申请单编号列表")
    private List<String> paymentAppList;

    @Schema(description = "费用名称")
    private String feeName;

    @Schema(description = "科目名称")
    private String financialSubjectName;

    @Schema(description = "出纳员")
    private UserDept cashier;

    @Schema(description = "类别配置主键")
    private Long dictSubjectId;

    @Schema(description = "发票金额")
    private JsonAmount invoiceAmount;

    @Schema(description = "发票附件")
    private List<SimpleFile> annex;

    @Schema(description = "累计支付金额")
    private JsonAmount totalPaymentAmount;

    @Schema(description = "累计发票金额")
    private JsonAmount totalInvoiceAmount;

    @Schema(description = "关联申请单列表")
    private List<SimplePaymentAppResp> paymentAppLinkList;

    @Schema(description = "发票标识")
    private Integer invoiceFlag;

    @Schema(description = "支付金额")
    private JsonAmount payAmount;

    @Schema(description = "寄件列表")
    private List<PaymentAppRespFeeShareVO> sendFeeShareList;

    @Schema(description = "船代费用列表")
    private List<PaymentAppRespFeeShareVO> forwarderFeeShareList;

    @Schema(description = "相关类别")
    private Integer relationType;

    @Schema(description = "相关编号列表")
    private List<String>  relationCode;

    @Schema(description = "打印日期")
    private LocalDateTime printDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建用户")
    private UserDept createUser;

    @Schema(description = "关联标识")
    private Integer linkFlag;
}