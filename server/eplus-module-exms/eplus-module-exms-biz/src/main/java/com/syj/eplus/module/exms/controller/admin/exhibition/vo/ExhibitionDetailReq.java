package com.syj.eplus.module.exms.controller.admin.exhibition.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ExhibitionDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "展会id", example = "1")
    private Long exhibitionId;

}