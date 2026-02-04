package com.syj.eplus.module.infra.convert.paymentitem;

import cn.iocoder.yudao.module.system.dal.dataobject.paymentplan.SystemPaymentPlan;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import com.syj.eplus.module.infra.controller.admin.paymentitem.vo.PaymentItemRespVO;
import com.syj.eplus.module.infra.dal.dataobject.paymentitem.PaymentItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PaymentItemConvert {

    PaymentItemConvert INSTANCE = Mappers.getMapper(PaymentItemConvert.class);

    PaymentItemRespVO convertPaymentResp(PaymentItemDO paymentItemDO);

    PaymentItemDTO convertPaymentDTO(PaymentItemDO paymentItemDO);

    List<PaymentItemDTO> convertPaymentDTOList(List<PaymentItemDO> paymentDOList);

    SystemPaymentPlanDTO convertPaymentPlanDTO(SystemPaymentPlan systemPaymentPlan);

    List<SystemPaymentPlanDTO> convertPaymentPlanDTOList(List<SystemPaymentPlan> systemPaymentPlans);

}
