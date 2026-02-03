package com.syj.eplus.module.dms.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.enums.api.ForwarderFeeApi;
import com.syj.eplus.module.dms.enums.api.dto.DmsForwarderFeeDTO;
import com.syj.eplus.module.dms.enums.api.dto.ForwarderFeeDTO;
import com.syj.eplus.module.dms.service.forwarderfee.ForwarderFeeService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ForwarderFeeApiImpl implements ForwarderFeeApi {
    @Resource
    @Lazy
    private ForwarderFeeService service;

    @Override
    public void updateStatusByCodeList(DmsForwarderFeeDTO dmsForwarderFeeDTO) {
        service.updateStatusByCodeList(dmsForwarderFeeDTO);

    }

    @Override
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
      return service.getFeeShareAmountByCode(businessCode);
    }

    @Override
    public void updateBelongFlagByCode(String businessKey, Integer value) {
        service.updateBelongFlagByCode(businessKey,value);
    }

    @Override
    public List<ForwarderFeeDTO> getListByPaymentCode(String code) {
        List<ForwarderFeeDO> dos = service.getListByPaymentCode(code);
        return BeanUtils.toBean(dos,ForwarderFeeDTO.class);
    }

    @Override
    public Map<String, ForwarderFeeDTO> getListByCodes(List<String> relationCodes) {
        List<ForwarderFeeDO> doList = service.getListByCodeList(relationCodes);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<ForwarderFeeDTO> feeDTOS = BeanUtils.toBean(doList, ForwarderFeeDTO.class);
        return feeDTOS.stream().collect(Collectors.toMap(ForwarderFeeDTO::getCode, s->s,(s1,s2)->s1));
    }

    @Override
    public Map<String, List<JsonAmount>> getAmountByShipmentCodes(List<String> relationCodes) {
        return service.getAmountByShipmentCodes(relationCodes);
    }

}
