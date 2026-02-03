package com.syj.eplus.module.oa.listener;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
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
import com.syj.eplus.module.oa.api.RepayAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.service.other.OtherReimbService;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.*;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-30
 * @Description: 其他费用报销监听
 */
@Component
public class OtherReimbAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private OtherReimbService otherReimbService;

    @Resource
    private ReimbService reimbService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private PaymentApi paymentApi;
    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private RepayAppApi repayAppApi;
    @Override
    protected String getProcessDefinitionKey() {
        return otherReimbService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long auditableId = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        reimbService.updateAuditStatus(auditableId, result,null);
        ReimbRespVO reimbResp = reimbService.getReimbResp(auditableId);
        // 只有通过的报销才会发起付款单与还款申请单
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
            if ((BpmProcessInstanceResultEnum.REJECT.getResult().equals(result)||BpmProcessInstanceResultEnum.CANCEL.getResult().equals(result))&&ReimbTypeEnum.OTHER.getValue().equals(reimbResp.getReimbType())){
               if(Objects.isNull(reimbResp.getCreator())){
                   throw exception(CREATOR_IS_NULL);
               }
                otherReimbService.updateApproveUserByInstanceId(auditableId,Long.parseLong(reimbResp.getCreator()));
            }
            return;
        }
        if (Objects.isNull(reimbResp)) {
            throw exception(TRAVEL_REIMB_NOT_EXISTS);
        }
        // 仅处理其他费用报销
        if (!ReimbTypeEnum.OTHER.getValue().equals(reimbResp.getReimbType())) {
            return;
        }
        List<JsonAmount> totalAmountList = reimbResp.getTotalAmountList();
        if (CollUtil.isEmpty(totalAmountList)){
            return;
        }
        UserDept reimbUser = reimbResp.getReimbUser();
        if (Objects.isNull(reimbUser)) {
            throw exception(REIMBER_NULL);
        }
        PaymentSaveDTO paymentSaveDTO = new PaymentSaveDTO().setApplyAmount(totalAmountList)
                .setBusinessType(BusinessTypeEnum.OTHER_APP.getCode())
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
        // 更新费用归属状态
        feeShareApi.updateSourceStatus(FeeShareSourceTypeEnum.OTHER_REIMBURSE.getValue(), auditableId,result);
    }

}
