package com.syj.eplus.module.mms.controller.admin.manufacture.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 加工单新增/修改 Request VO")
@Data
public class ManufactureSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6654")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "录入人id", example = "19805")
    private Long inputUserId;

    @Schema(description = "录入人姓名", example = "王五")
    private String inputUserName;

    @Schema(description = "录入时间")
    private LocalDateTime inputTime;


    @Schema(description = "仓库id", example = "7356")
    private Long stockId;

    @Schema(description = "仓库名称", example = "赵六")
    private String stockName;

    @Schema(description = "主体id", example = "20710")
    private Long companyId;

    @Schema(description = "主体名称", example = "赵六")
    private String companyName;

    @Schema(description = "客户id", example = "19474")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称", example = "李四")
    private String custName;

    @Schema(description = "加工单状态", example = "2")
    private Integer manufactureStatus;

    @Schema(description = "是否自动生成")
    private Integer autoFlag;

    @Schema(description = "完成加工时间")
    private LocalDateTime doneTime;

    @Schema(description = "结案时间")
    private LocalDateTime finishTime;

    @Schema(description = "结案原因")
    private String finishDesc;

    @Schema(description = "销售合同号")
    private String saleContractCode;

}