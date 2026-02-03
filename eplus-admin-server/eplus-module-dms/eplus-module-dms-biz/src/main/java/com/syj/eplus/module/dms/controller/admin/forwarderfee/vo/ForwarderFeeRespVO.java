package com.syj.eplus.module.dms.controller.admin.forwarderfee.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "管理后台 - 船代费用 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ForwarderFeeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5709")
    @ExcelProperty("主键")
    private Long id;
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5709")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "出运费用主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26547")
    @ExcelProperty("出运费用主键")
    private Long shipmentId;
    
    @Schema(description = "出运费用编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("出运费用编号")
    private String shipmentCode;
    
    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;
    
    @Schema(description = "供应商主键", example = "2865")
    @ExcelProperty("供应商主键")
    private Long venderId;
    
    @Schema(description = "供应商编号")
    @ExcelProperty("供应商编号")
    private String venderCode;
    
    @Schema(description = "供应商名称", example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;
    
    @Schema(description = "费用名称主键", example = "26063")
    @ExcelProperty("费用名称主键")
    private Long dictSubjectId;
    
    @Schema(description = "费用名称", example = "芋艿")
    @ExcelProperty("费用名称")
    private String dictSubjectName;
    
    @Schema(description = "费用类型", example = "2")
    @ExcelProperty("费用类型")
    private Integer feeType;
    
    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("金额")
    private JsonAmount amount;
    
    @Schema(description = "付款状态", example = "2")
    @ExcelProperty("付款状态")
    private Integer payStatus;
    
    @Schema(description = "申请人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请人")
    private UserDept applyer;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "对公申请主键")
    private Long paymentAppId;

    @Schema(description = "对公申请编号")
    private String paymentAppCode;

    @Schema(description = "费用归集数据")
    private List<FeeShareRespDTO> feeShare;
    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付日期")
    private String paymentDate;

}