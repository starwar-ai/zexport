package com.syj.eplus.module.fms.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.PaymentRespVO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.PaymentSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.payment.PaymentDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface PaymentConvert {

    PaymentConvert INSTANCE = Mappers.getMapper(PaymentConvert.class);

    @Mapping(target = "applyerId", ignore = true)
    PaymentSaveReqVO convertPaymentSaveReqVO(PaymentSaveDTO paymentSaveDTO);


    default List<PaymentSaveReqVO> convertPaymentSaveReqVOList(List<PaymentSaveDTO> paymentSaveDTOList) {
        return CollectionUtils.convertList(paymentSaveDTOList, paymentSaveDTO -> {
            PaymentSaveReqVO paymentSaveReqVO = convertPaymentSaveReqVO(paymentSaveDTO);
            paymentSaveReqVO.setSubmitFlag(SubmitFlagEnum.ONLY_SAVE.getStatus());
            return paymentSaveReqVO;
        });
    }

    default PaymentRespVO convertPaymentRespVO(PaymentDO paymentDO, Map<Long, UserDept> userDeptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap) {
        PaymentRespVO paymentRespVO = BeanUtils.toBean(paymentDO, PaymentRespVO.class);
        if (CollUtil.isNotEmpty(userDeptMap)) {
            paymentRespVO.setInputUser(userDeptMap.get(Long.parseLong(paymentDO.getCreator())));
        }
        paymentRespVO.setCashier(paymentDO.getCashier());
        if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
            SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(paymentDO.getCompanyId());
            if (Objects.nonNull(simpleCompanyDTO)) {
                paymentRespVO.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
        }
        return paymentRespVO;
    }


    default PageResult<PaymentRespVO> convertPaymentPageVO(PageResult<PaymentDO> pageResult, Map<Long, UserDept> userDeptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap) {
        List<PaymentDO> paymentDOList = pageResult.getList();
        Long total = pageResult.getTotal();
        if (CollUtil.isEmpty(paymentDOList)) {
            return new PageResult<>();
        }
        return new PageResult<>(CollectionUtils.convertList(paymentDOList, paymentDO -> convertPaymentRespVO(paymentDO, userDeptMap, simpleCompanyDTOMap)), total);
    }
}
