package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanItemToContractRespVO extends  PurchasePlanItemRespVO {

    @Schema(description = "组套件明细列表")
    private List<PurchasePlanItemToContractItemRespVO> combineList;

    @Schema(description = "采购单来源")
    private Integer sourceType;

    @Schema(description = "采购员列表")
    private List<UserDept> purchaseUserList;
}