package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 开票通知新增/修改 Request VO")
@Data
public class InvoicingNoticesSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15536")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "归属公司主键", example = "12936")
    private Long companyId;

    @Schema(description = "归属公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String companyName;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept inputUser;

    @Schema(description = "录入日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inputDate;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String venderName;

    @Schema(description = "跟单员", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept manager;

    @Schema(description = "出运单号")
    private String shipmentCode;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer printFlag;

    @Schema(description = "采购单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purOrderCode;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "是否手动生成", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer manuallyFlag;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "来源类型")
    private Integer sourceType;

    @Schema(description = "开票通知明细")
    private List<InvoicingNoticesItem> children;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;
}