package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryPurchaseContractDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemAndPlanRespVO;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.paymentplan.PurchasePaymentPlan;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 采购合同 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class PurchaseContractInfoRespVO extends PurchaseContractRespVO {

    @Schema(description = "采购计划关联列表")
    public List<PurchasePlanItemAndPlanRespVO> purchasePlanList;

    @Schema(description = "变更单")
    public List<PurchaseContractChange> changeList;

    @Schema(description = "入库单")
    public List<StockNoticeRespDTO> warehousingList;

    @Schema(description = "验货单")
    public List<Object> checkList;


    @Schema(description = "采购计划明细列表")
    private List<PurchaseContractItemInfoRespVO> children;

    @Schema(description = "产品种类")
    private Long skuTypeCount;

    @Schema(description = "产品总数量")
    private Integer skuCount;

    @Schema(description = "采购总金额")
    private List<JsonAmount> summary;

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "产品总到货数量")
    private Integer totalReceivedCount;

    @Schema(description = "产品总验收数量")
    private Integer totalCheckedCount;

    @Schema(description = "产品总退货数量")
    private Integer totalReturnCount;

    @Schema(description = "产品总换货数量")
    private Integer totalExchangeCount;

    @Schema(description = "总金额")
    private List<JsonAmount> totalAmountCurrency;


    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;


    @Schema(description = "供应商联系人")
    private VenderPocDO venderPoc;

    @Schema(description = "加减项列表")
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

    @Schema(description = "付款方式列表")
    private List<PaymentItemDTO> paymentList;

    @Schema(description = "付款计划")
    private List<PurchasePaymentPlan> purchasePaymentPlanList;

    @Schema(description = "辅料分摊列表")
    private List<AuxiliaryPurchaseContractDTO> allocationList;

    @Schema(description = "回签附件必填标记")
    private Integer signBackAnnexNotNullFlag;

    @Schema(description = "应付供应商")
    private List<SimpleData> venderlink;

    @Schema(description = "包材附件")
    private List<SimpleFile> auxiliaryAnnex;
}
