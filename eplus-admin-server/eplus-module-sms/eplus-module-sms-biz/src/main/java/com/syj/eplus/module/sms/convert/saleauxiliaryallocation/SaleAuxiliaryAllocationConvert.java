package com.syj.eplus.module.sms.convert.saleauxiliaryallocation;

import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.SaleAuxiliaryAllocationRespVO;
import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.SaleAuxiliaryAllocationSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SaleAuxiliaryAllocationConvert {

        SaleAuxiliaryAllocationConvert INSTANCE = Mappers.getMapper(SaleAuxiliaryAllocationConvert.class);

        SaleAuxiliaryAllocationRespVO convert(SaleAuxiliaryAllocationDO saleAuxiliaryAllocationDO);

        default SaleAuxiliaryAllocationRespVO convertSaleAuxiliaryAllocationRespVO(SaleAuxiliaryAllocationDO saleAuxiliaryAllocationDO){
                SaleAuxiliaryAllocationRespVO saleAuxiliaryAllocationRespVO = convert(saleAuxiliaryAllocationDO);
                return saleAuxiliaryAllocationRespVO;
        }

    SaleAuxiliaryAllocationDO convertSaleAuxiliaryAllocationDO(SaleAuxiliaryAllocationSaveReqVO saveReqVO);
}