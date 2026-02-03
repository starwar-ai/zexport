package com.syj.eplus.module.scm.controller.admin.purchaseplan.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemAndContractInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemToContractSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购计划 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanInfoRespVO extends PurchasePlanRespVO {

    @Schema(description = "采购合同列表")
    @ExcelIgnore
    private List<PurchaseContractItemAndContractInfoRespVO> contractList;

    @Schema(description = "采购计划明细列表")
    private List<PurchasePlanItemInfoRespVO> children;

    @Schema(description = "产品种类")
    private Long skuTypeCount;

    @Schema(description = "产品总数量")
    private Integer skuCount;

    @Schema(description = "采购总金额")
    private List<JsonAmount> summary;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "采购计划列表")
    private List<PurchasePlanItemToContractSaveReqVO> planList;

    @Schema(description = "组装单列表")
    private List<PurchasePlanItemToContractSaveReqVO> combineList;

    @Schema(description = "加工资质企业主键列表")
    private List<Long> producedCompanyIdList;

    @Schema(description = "当前主体可用币种")
    private List<String> companyCurrencyList;

    @Schema(description = "采购主体类别")
    private  Integer companyNature;
}
