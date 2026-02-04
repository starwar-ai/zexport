package com.syj.eplus.module.dms.controller.admin.forwarderfee.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 船代费用新增/修改 Request VO")
@Data
public class ForwarderFeeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5709")
    private Long id;

    @Schema(description = "出运费用主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26547")
    @NotNull(message = "出运费用主键不能为空")
    private Long shipmentId;

    @Schema(description = "出运费用编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "出运费用编号不能为空")
    private String shipmentCode;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "供应商主键", example = "2865")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称", example = "张三")
    private String venderName;

    @Schema(description = "费用名称主键", example = "26063")
    private Long dictSubjectId;

    @Schema(description = "费用名称", example = "芋艿")
    private String dictSubjectName;

    @Schema(description = "费用类型", example = "2")
    private Integer feeType;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "金额不能为空")
    private JsonAmount amount;

    @Schema(description = "付款状态", example = "2")
    private Integer payStatus;

    @Schema(description = "申请人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "申请人不能为空")
    private UserDept applyer;

    @Schema(description = "对公申请主键")
    private Long paymentAppId;

    @Schema(description = "对公申请编号")
    private String paymentAppCode;

}