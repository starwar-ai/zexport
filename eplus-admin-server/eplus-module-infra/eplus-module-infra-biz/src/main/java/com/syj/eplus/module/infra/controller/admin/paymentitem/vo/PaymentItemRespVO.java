package com.syj.eplus.module.infra.controller.admin.paymentitem.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 付款方式 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9949")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "名称", example = "李四")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("bpm_process_instance_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer auditStatus;

    @Schema(description = "起始日类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "起始日类型", converter = DictConvert.class)
    @DictFormat("date_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer dateType;

    @Schema(description = "天数")
    @ExcelProperty("天数")
    private Integer duration;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "付款计划")
    private List<SystemPaymentPlanDTO> systemPaymentPlanList;

}