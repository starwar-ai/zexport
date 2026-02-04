package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 开票通知 Response VO")
@Data
@ExcelIgnoreUnannotated
public class InvoicingNoticesRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15536")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "归属公司主键", example = "12936")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入人")
    private UserDept inputUser;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "跟单员", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("跟单员")
    private UserDept manager;

    @Schema(description = "出运单号")
    @ExcelProperty("出运单号")
    private String shipmentCode;

    @Schema(description = "出运日期")
    @ExcelProperty("出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印状态")
    private Integer printFlag;

    @Schema(description = "采购单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采购单号")
    private String purOrderCode;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "是否手动生成", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否手动生成")
    private Integer manuallyFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "开票通知明细")
    private List<InvoicingNoticesItem> children;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "打印日期")
    private LocalDateTime printDate;

    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "流程实例主键")
    private String processInstanceId;

    @Schema(description = "数据来源")
    private Integer sourceType;

    @Schema(description = "登票日期")
    private LocalDateTime registrationDate;
}