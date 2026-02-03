package com.syj.eplus.module.oa.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.framework.common.enums.PaymentAppTypeEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.PaymentAppDetailReq;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.PaymentAppRespVO;
import com.syj.eplus.module.oa.converter.PaymentAppConvert;
import com.syj.eplus.module.oa.service.paymentapp.PaymentAppService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.PAYMENT_APP_NOT_EXISTS;

@Component
public class PaymentappAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PaymentAppService paymentAppService;
    @Resource
    private PaymentApi paymentApi;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private FeeShareApi feeShareApi;

    @Override
    protected String getProcessDefinitionKey() {
        return paymentAppService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long paymentAppId = Long.parseLong(event.getBusinessKey());
        PaymentAppRespVO paymentApp = paymentAppService.getPaymentApp(new PaymentAppDetailReq().setPaymentAppId(paymentAppId));
        if (Objects.isNull(paymentApp)){
            throw exception(PAYMENT_APP_NOT_EXISTS);
        }
        paymentAppService.updateAuditStatus(paymentAppId, event.getResult());
        if (PaymentAppTypeEnum.OFF_SET.getType().equals(paymentApp.getPrepaidFlag())||Objects.isNull(paymentApp.getAmount())||Objects.isNull(paymentApp.getAmount().getAmount())|| BigDecimal.ZERO.compareTo(paymentApp.getAmount().getAmount())==0){
         return;
        }
        // 更新费用归属状态
        feeShareApi.updateSourceStatus(FeeShareSourceTypeEnum.PAYMENT_APP.getValue(), paymentAppId, event.getResult());
        if (!event.getResult().equals(BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            return;
        }
        // 预付无需创建付款单
//        if (BooleanEnum.YES.getValue().equals(paymentApp.getPrepaidFlag())){
//            return;
//        }
        // 创建付款单
        PaymentSaveDTO paymentSaveDTO = PaymentAppConvert.Instance.convertPaymentSaveDTO(paymentApp);
        paymentSaveDTO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
        paymentSaveDTO.setBusinessType(BusinessTypeEnum.PAYMENT_APP.getCode());
        paymentSaveDTO.setBusinessCode(paymentApp.getCode());
        paymentSaveDTO.setBusinessSubjectCode(paymentApp.getBusinessSubjectCode());
        paymentSaveDTO.setBusinessSubjectType(paymentApp.getBusinessSubjectType());
        paymentSaveDTO.setCompanyId(paymentApp.getCompanyId());
        paymentSaveDTO.setApplyAmount(Collections.singletonList(paymentApp.getAmount()));
        paymentSaveDTO.setApplyer(paymentApp.getApplyer());
        paymentSaveDTO.setApplyPaymentDate(paymentApp.getCreateTime());
        paymentApi.createPayment(paymentSaveDTO);

    }
}