package com.syj.eplus.module.exms.controller.admin.exhibition.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 展会新增/修改 Request VO")
@Data
public class ExhibitionDoneReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31339")
    private Long id;


    @Schema(description = "实际日期")
    private LocalDateTime[] date;

}