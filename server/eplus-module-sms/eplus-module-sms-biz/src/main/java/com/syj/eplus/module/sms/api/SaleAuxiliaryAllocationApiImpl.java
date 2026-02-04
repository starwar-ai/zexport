package com.syj.eplus.module.sms.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.sms.api.dto.SaleAuxiliaryAllocationDTO;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;
import com.syj.eplus.module.sms.service.saleauxiliaryallocation.SaleAuxiliaryAllocationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SaleAuxiliaryAllocationApiImpl implements SaleAuxiliaryAllocationApi {
    @Resource
    private SaleAuxiliaryAllocationService service;


    @Override
    public Map<Long,List<SaleAuxiliaryAllocationDTO>>  getListBySaleCode(String code) {
        List<SaleAuxiliaryAllocationDO> doList = service.getListBySaleCode(code);

        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<SaleAuxiliaryAllocationDTO> dtoList = BeanUtils.toBean(doList, SaleAuxiliaryAllocationDTO.class);
        return dtoList.stream().collect(Collectors.groupingBy(SaleAuxiliaryAllocationDTO::getAuxiliaryPurchaseContractItemId));
    }
}
