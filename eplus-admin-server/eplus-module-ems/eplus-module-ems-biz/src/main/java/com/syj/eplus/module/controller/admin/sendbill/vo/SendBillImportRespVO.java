package com.syj.eplus.module.controller.admin.sendbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 寄件导入单据 Response VO")
@Data
public class SendBillImportRespVO {

    @Schema(description = "正常导入的数据列表")
    private List<SendBillRespVO> successList;

    @Schema(description = "输入错误无法导入的数据列表")
    private List<SendBillRespVO> errorList;

    @Schema(description = "不能处理的数据列表")
    private List<SendBillRespVO> finishList;

}