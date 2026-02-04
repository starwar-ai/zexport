package com.syj.eplus.module.oa.converter;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BusinessSubjectTypeEnum;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.PaymentAppRespVO;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.SimplePaymentAppResp;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface PaymentAppConvert {

    PaymentAppConvert Instance = Mappers.getMapper(PaymentAppConvert.class);

    PaymentAppRespVO convert(PaymentAppDO paymentAppDO);
    default PaymentAppRespVO convert(PaymentAppDO paymentAppDO, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<Long, String> subjectMap,Map<Long, DictSubjectDO> dictSubjectMap,Map<Long, UserDept> userDeptCache) {
        PaymentAppRespVO paymentAppRespVO = convert(paymentAppDO);
        Long companyId = paymentAppDO.getCompanyId();
        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
        if (Objects.nonNull(simpleCompanyDTO)) {
            paymentAppRespVO.setCompanyName(simpleCompanyDTO.getCompanyName());
        }
        if (CollUtil.isNotEmpty(subjectMap)){
            paymentAppRespVO.setFinancialSubjectName(subjectMap.get(paymentAppDO.getFinancialSubjectId()));
        }
        if (CollUtil.isNotEmpty(dictSubjectMap)){
            DictSubjectDO dictSubjectDO = dictSubjectMap.get(paymentAppDO.getDictSubjectId());
            if(Objects.nonNull(dictSubjectDO)){
                paymentAppRespVO.setFeeName(dictSubjectDO.getFeeName());
            }
        }
        if (CollUtil.isNotEmpty(userDeptCache)){
            paymentAppRespVO.setCreateUser(userDeptCache.get(Long.valueOf(paymentAppDO.getCreator())));
        }
        return paymentAppRespVO;
    }

    default PageResult<PaymentAppRespVO> convertPageResultVO(PageResult<PaymentAppDO> paymentAppDOPageResult, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<Long, String> subjectMap,Map<Long, DictSubjectDO> dictSubjectMap,Map<Long, UserDept> userDeptCache) {
        List<PaymentAppDO> paymentAppDOList = paymentAppDOPageResult.getList();
        Long total = paymentAppDOPageResult.getTotal();
        if (CollUtil.isEmpty(paymentAppDOList)){
            return null;
        }
        List<PaymentAppRespVO> paymentAppRespVOList = CollectionUtils.convertList(paymentAppDOList, paymentAppDO -> convert(paymentAppDO, simpleCompanyDTOMap, subjectMap, dictSubjectMap,userDeptCache));
        return new PageResult<>(paymentAppRespVOList, total);
    }

    default PaymentSaveDTO convertPaymentSaveDTO(PaymentAppRespVO paymentApp) {
        PaymentSaveDTO paymentSaveDTO = BeanUtils.toBean(paymentApp, PaymentSaveDTO.class);
        List<JsonAmount> jsonAmountList = Collections.singletonList(paymentApp.getAmount());
        paymentSaveDTO.setApplyAmount(jsonAmountList);
        paymentSaveDTO.setAmount(null);
        return paymentSaveDTO;
    }
    default SimplePaymentAppResp convertSimplePayment(PaymentAppDO paymentAppDO,Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<String, CustAllDTO> custAllDTOMap,Map<String, VenderAllDTO> venderMap){
        SimplePaymentAppResp simplePaymentAppResp = BeanUtils.toBean(paymentAppDO, SimplePaymentAppResp.class);
        Long companyId = paymentAppDO.getCompanyId();
        if (CollUtil.isNotEmpty(simpleCompanyDTOMap)){
            SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
            if (Objects.nonNull(simpleCompanyDTO)){
                simplePaymentAppResp.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
            if (CollUtil.isNotEmpty(custAllDTOMap)&&BusinessSubjectTypeEnum.CUST.getCode().equals(paymentAppDO.getBusinessSubjectType())){
                CustAllDTO cust = custAllDTOMap.get(paymentAppDO.getBusinessSubjectCode());
                if (Objects.nonNull(cust)){
                    simplePaymentAppResp.setBusinessSubjectName(cust.getName());
                }
            }
            if (CollUtil.isNotEmpty(venderMap)&&BusinessSubjectTypeEnum.VENDER.getCode().equals(paymentAppDO.getBusinessSubjectType())){
                VenderAllDTO vender = venderMap.get(paymentAppDO.getBusinessSubjectCode());
                if (Objects.nonNull(vender)){
                    simplePaymentAppResp.setBusinessSubjectName(vender.getName());
                }
            }
            simplePaymentAppResp.setTotalPaymentAmount(paymentAppDO.getTotalPaymentAmount());
        }
        return simplePaymentAppResp;
    }

    default List<SimplePaymentAppResp> convertSimplePaymentList(List<PaymentAppDO> paymentAppDOList,Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<String, CustAllDTO> custAllDTOMap,Map<String, VenderAllDTO> venderMap){
        return CollectionUtils.convertList(paymentAppDOList,paymentAppDO -> convertSimplePayment(paymentAppDO,simpleCompanyDTOMap,custAllDTOMap,venderMap));
    }
}
