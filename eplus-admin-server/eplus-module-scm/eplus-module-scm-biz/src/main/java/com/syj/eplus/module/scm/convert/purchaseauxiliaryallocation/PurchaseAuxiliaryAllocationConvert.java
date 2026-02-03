package com.syj.eplus.module.scm.convert.purchaseauxiliaryallocation;

import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.PurchaseAuxiliaryAllocationSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PurchaseAuxiliaryAllocationConvert {

        PurchaseAuxiliaryAllocationConvert INSTANCE = Mappers.getMapper(PurchaseAuxiliaryAllocationConvert.class);

        PurchaseAuxiliaryAllocationRespVO convert(PurchaseAuxiliaryAllocationDO purchaseAuxiliaryAllocationDO);

        default PurchaseAuxiliaryAllocationRespVO convertPurchaseAuxiliaryAllocationRespVO(PurchaseAuxiliaryAllocationDO purchaseAuxiliaryAllocationDO){
                PurchaseAuxiliaryAllocationRespVO purchaseAuxiliaryAllocationRespVO = convert(purchaseAuxiliaryAllocationDO);
                return purchaseAuxiliaryAllocationRespVO;
        }

    PurchaseAuxiliaryAllocationDO convertPurchaseAuxiliaryAllocationDO(PurchaseAuxiliaryAllocationSaveReqVO saveReqVO);
}