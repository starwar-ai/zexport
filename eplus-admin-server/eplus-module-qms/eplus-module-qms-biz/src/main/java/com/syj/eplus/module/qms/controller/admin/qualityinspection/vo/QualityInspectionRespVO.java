package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 验货单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QualityInspectionRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2015")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单号")
    private String code;

    @Schema(description = "审核状态", example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "让步放行标记", example = "0")
    @ExcelProperty("让步放行标记")
    private Integer concessionReleaseFlag;

    @Schema(description = "验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案", example = "1")
    @ExcelProperty("验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案")
    private Integer qualityInspectionStatus;

    @Schema(description = "是否重验单:0：否，1：是")
    @ExcelProperty("是否重验单:0：否，1：是")
    private Integer reinspectionFlag;

    @Schema(description = "关联验货单id", example = "16132")
    @ExcelProperty("关联验货单id")
    private Long sourceId;

    @Schema(description = "关联验货单单号", example = "16132")
    @ExcelProperty("关联验货单单号")
    private String sourceCode;

    @Schema(description = "是否返工:0：否，1：是")
    @ExcelProperty("是否返工:0：否，1：是")
    private Integer reworkFlag;

    @Schema(description = "返工证据")
    @ExcelProperty("返工证据")
    private List<SimpleFile> reworkPicture;

    @Schema(description = "返工说明")
    @ExcelProperty("返工说明")
    private String reworkDesc;

    @Schema(description = "单据来源:1：采购合同", example = "1")
    @ExcelProperty("单据来源:1：采购合同")
    private Integer sourceType;

    @Schema(description = "采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "验货仓库主键")
    private Long warehouseId;

    @Schema(description = "验货仓库名称")
    private String warehouseName;

    @Schema(description = "验货方式:1： 泛太陪验（工厂） " +
                                "2：泛太陪验（公司内） \n" +
                                "3：泛太自验（工厂） \n" +
                                "4：泛太自验（公司内） \n" +
                                "5：客户自检 \n" +
                                "6：客户指定第三方 \n" +
                                "7：远程验货", example = "1")
    @ExcelProperty("验货方式")
    private Integer inspectionType;

    @Schema(description = "供应商id", example = "11989")
    @ExcelProperty("供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    @ExcelProperty("供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "申请验货人", example = "20824")
    @ExcelProperty("申请验货人")
    private Long applyInspectorId;

    @Schema(description = "申请验货人姓名", example = "赵六")
    @ExcelProperty("申请验货人姓名")
    private String applyInspectorName;

    @Schema(description = "申请验货人部门主键", example = "22439")
    @ExcelProperty("申请验货人部门主键")
    private Long applyInspectorDeptId;

    @Schema(description = "申请验货人部门名称", example = "芋艿")
    @ExcelProperty("申请验货人部门名称")
    private String applyInspectorDeptName;

    @Schema(description = "工厂联系人")
    private String factoryContacter;

    @Schema(description = "联系电话")
    private String factoryTelephone;

    @Schema(description = "验货地址")
    private String inspectionAddress;

    @Schema(description = "验货人", example = "26453")
    @ExcelProperty("验货人")
    private Long inspectorId;

    @Schema(description = "验货人姓名", example = "芋艿")
    @ExcelProperty("验货人姓名")
    private String inspectorName;

    @Schema(description = "验货人部门主键", example = "2378")
    @ExcelProperty("验货人部门主键")
    private Long inspectorDeptId;

    @Schema(description = "验货人部门名称", example = "王五")
    @ExcelProperty("验货人部门名称")
    private String inspectorDeptName;

    @Schema(description = "期望验货时间")
    private LocalDateTime expectInspectionTime;

    @Schema(description = "计划验货时间")
    @ExcelProperty("计划验货时间")
    private LocalDateTime planInspectionTime;

    @Schema(description = "实际验货时间")
    @ExcelProperty("实际验货时间")
    private LocalDateTime inspectionTime;

    @Schema(description = "特别注意事项")
    @ExcelProperty("特别注意事项")
    private String specialAttentionNotice;

    @Schema(description = "验货费用")
    @ExcelProperty("验货费用")
    private JsonAmount amount;

    @Schema(description = "验货费用分摊方式：1-按金额分摊 2-按数量分摊")
    private Integer allocationType;

    @Schema(description = "附件")
    @ExcelProperty("附件")
    private List<SimpleFile> annex;

    @Schema(description = "结果附件")
    @ExcelProperty("结果附件")
    private List<SimpleFile> resultAnnex;
    @Schema(description = "图片")
    @ExcelProperty("图片")
    private List<SimpleFile> picture;
    @Schema(description = "工厂保函")
    @ExcelProperty("工厂保函")
    private List<SimpleFile> guaranteeLetter;

    @Schema(description = "接受说明")
    @ExcelProperty("接受说明")
    private String acceptDesc;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称")
    private String companyName;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "验货单-明细 ")
    private List<QualityInspectionItemRespVO> children;


    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;


    @Schema(description = "处理状态")
    private Integer handleState;

    @Schema(description = "验货节点")
    private Integer inspectionNode;

    @Schema(description = "采购员")
    private UserDept purchaseUser;

    @Schema(description = "业务员")
    private UserDept sales;

}
