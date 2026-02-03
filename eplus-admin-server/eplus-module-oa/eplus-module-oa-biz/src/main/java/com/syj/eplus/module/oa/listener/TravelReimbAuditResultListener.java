package com.syj.eplus.module.oa.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
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
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbDetailReq;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.convert.travel.TravelReimbConvert;
import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import com.syj.eplus.module.oa.service.travelreimb.TravelReimbService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REIMBER_NULL;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_REIMB_NOT_EXISTS;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Component
public class TravelReimbAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private TravelReimbService travelReimbService;

    @Resource
    private PaymentApi paymentApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private RepayAppApi repayAppApi;
    @Resource
    private ReimbService reimbService;

    @Resource
    private FeeShareApi feeShareApi;
    @Override
    protected String getProcessDefinitionKey() {
        return travelReimbService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long auditableId = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        reimbService.updateAuditStatus(auditableId, result,null);
        // 只有通过的报销才会发起付款单与还款申请单
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
            return;
        }
        ReimbRespVO reimbResp = reimbService.getReimbResp(auditableId);
        if (Objects.isNull(reimbResp)) {
            throw exception(TRAVEL_REIMB_NOT_EXISTS);
        }
        // 仅处理出差报销
        if (!ReimbTypeEnum.TRAVEL.getValue().equals(reimbResp.getReimbType())) {
            return;
        }
        TravelReimbRespVO travelReimb = travelReimbService.getTravelReimb(new TravelReimbDetailReq().setTravelReimbId(auditableId));
        // 创建支付单
        Tuple travelAmountAndLoanAmount = travelReimbService.calcTravelAmount(TravelReimbConvert.INSTANCE.convertTravelSaveByResp(travelReimb));
        Map<String, BigDecimal> travelReimbAmountMap = travelAmountAndLoanAmount.get(0);
        UserDept reimbUser = travelReimb.getReimbUser();
        if (Objects.isNull(reimbUser)) {
            throw exception(REIMBER_NULL);
        }
        if (CollUtil.isNotEmpty(travelReimbAmountMap)) {
            List<JsonAmount> list = travelReimbAmountMap.entrySet().stream().map(entry -> new JsonAmount().setCurrency(entry.getKey()).setAmount(entry.getValue()))
                    .toList();
            PaymentSaveDTO paymentSaveDTO = new PaymentSaveDTO().setApplyAmount(list)
                    .setBusinessType(BusinessTypeEnum.TRAVEL_APP.getCode())
                    .setBusinessCode(travelReimb.getCode())
                    .setBusinessSubjectType(BusinessSubjectTypeEnum.TRAVEL_REMIB.getCode())
                    .setBusinessSubjectCode(String.valueOf(reimbUser.getUserId()))
                    .setCompanyId(reimbResp.getCompanyId())
                    .setApplyPaymentDate(reimbResp.getCreateTime())
                    .setApplyer(reimbResp.getReimbUser());
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
//        // 创建还款申请
//        List<RepayAppDTO> repayAppDTOList = travelAmountAndLoanAmount.get(1);
//        if (CollUtil.isNotEmpty(repayAppDTOList)) {
//            repayAppDTOList.forEach(repayAppDTO -> {
//                repayAppApi.createRepayApp(repayAppDTO);
//            });
//        }
        // 更新费用归属状态
        feeShareApi.updateSourceStatus(FeeShareSourceTypeEnum.TRAVEL_REIMBURSE.getValue(), Long.parseLong(event.getBusinessKey()),event.getResult());
    }
}
