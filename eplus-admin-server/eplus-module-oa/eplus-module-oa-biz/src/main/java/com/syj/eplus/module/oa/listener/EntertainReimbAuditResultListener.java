package com.syj.eplus.module.oa.listener;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BusinessSubjectTypeEnum;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.framework.common.enums.ReimbTypeEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.api.LoanAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import com.syj.eplus.module.oa.service.entertain.EntertainReimbService;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.ENTERTAIN_REIMVB_DELETE_ERROR;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REIMBER_NULL;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/25 10:51
 */
@Component
public class EntertainReimbAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private EntertainReimbService entertainReimbService;
    @Resource
    private ReimbService reimbService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private PaymentApi paymentApi;

    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private LoanAppApi loanAppApi;
    @Override
    protected String getProcessDefinitionKey() {
        return entertainReimbService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {

        long auditableId = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        reimbService.updateAuditStatus(auditableId, result,null);
        ReimbRespVO reimbResp = reimbService.getReimbResp(auditableId);
        if (Objects.isNull(reimbResp)) {
            throw exception(ENTERTAIN_REIMVB_DELETE_ERROR);
        }

//        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
//            reimbService.updateInvoiceHolderStatus(reimbResp.getInvoiceHolderId(), ReimbStatusEnum.REIMBURSED.getValue());
//        }else {
//            reimbService.updateInvoiceHolderStatus(reimbResp.getInvoiceHolderId(), ReimbStatusEnum.UNDER_REIMBURSEMENT.getValue());
//            return;
//        }
        // 仅处理招待费报销
        if (!ReimbTypeEnum.ENTERTAIN.getValue().equals(reimbResp.getReimbType())) {
            return;
        }
        EntertainReimbRespVO entertainReimb = entertainReimbService.getEntertainReimb(new EntertainReimbDetailReq().setEntertainReimbId(auditableId));
        if (Objects.isNull(entertainReimb)){
            throw exception(ENTERTAIN_REIMVB_DELETE_ERROR);
        }
        List<JsonReimbDetail> reimbDetailList = entertainReimb.getReimbDetailList();
        UserDept reimbUser = reimbResp.getReimbUser();
        if (Objects.isNull(reimbUser)) {
            throw exception(REIMBER_NULL);
        }
        // 创建支付单
        Map<String, BigDecimal> stringBigDecimalMap = entertainReimbService.calcTotalAmount(reimbDetailList);
        if (CollUtil.isNotEmpty(stringBigDecimalMap)) {
            List<JsonAmount> list = stringBigDecimalMap.entrySet().stream().map(entry -> new JsonAmount().setCurrency(entry.getKey()).setAmount(entry.getValue()))
                    .toList();
            PaymentSaveDTO paymentSaveDTO = new PaymentSaveDTO().setApplyAmount(list)
                    .setBusinessType(BusinessTypeEnum.ENTERTAIN_APP.getCode())
                    .setBusinessCode(reimbResp.getCode())
                    .setBusinessSubjectType(BusinessSubjectTypeEnum.TRAVEL_REMIB.getCode())
                    .setBusinessSubjectCode(String.valueOf(reimbUser.getUserId()))
                    .setCompanyId(reimbResp.getCompanyId())
                    .setApplyPaymentDate(reimbResp.getCreateTime())
                    .setApplyer(reimbUser);
            Map<Long, UserBackAccountDTO> userBackAccountDTOMap = adminUserApi.getUserBackAccountDTOMap(reimbUser.getUserId());
            if (CollUtil.isNotEmpty(userBackAccountDTOMap)) {
                UserBackAccountDTO userBackAccountDTO = userBackAccountDTOMap.get(reimbUser.getUserId());
                if (Objects.nonNull(userBackAccountDTO)) {
                    paymentSaveDTO.setBank(userBackAccountDTO.getBank())
                            .setBankAccount(userBackAccountDTO.getBankAccount())
                            .setBankPoc(userBackAccountDTO.getBankPoc())
                            .setBankAddress(userBackAccountDTO.getBankAddress())
                            .setBankCode(userBackAccountDTO.getBankCode());
                }
            }
            List<JsonAmount> applyAmount = paymentSaveDTO.getApplyAmount();
            boolean match = applyAmount.stream().allMatch(s -> Objects.isNull(s) || Objects.isNull(s.getAmount()) || s.getAmount().compareTo(BigDecimal.ZERO) == 0);
            if (match){
                reimbService.repayLoanApp(auditableId);
            }else {
                paymentApi.createPayment(paymentSaveDTO);
            }
        }
//        reimbService.dealRepayApp(reimbResp);
        // 更新费用归属状态
        feeShareApi.updateSourceStatus(FeeShareSourceTypeEnum.GENERAL_REIMBURSE.getValue(), auditableId,result);
    }
}
