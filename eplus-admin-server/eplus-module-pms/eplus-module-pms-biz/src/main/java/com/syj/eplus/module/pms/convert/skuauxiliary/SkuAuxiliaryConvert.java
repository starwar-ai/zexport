package com.syj.eplus.module.pms.convert.skuauxiliary;

import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryRespVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliarySaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SkuAuxiliaryConvert {

        SkuAuxiliaryConvert INSTANCE = Mappers.getMapper(SkuAuxiliaryConvert.class);

        SkuAuxiliaryRespVO convert(SkuAuxiliaryDO skuAuxiliaryDO);

        default SkuAuxiliaryRespVO convertSkuAuxiliaryRespVO(SkuAuxiliaryDO skuAuxiliaryDO){
                SkuAuxiliaryRespVO skuAuxiliaryRespVO = convert(skuAuxiliaryDO);
                return skuAuxiliaryRespVO;
        }

    SkuAuxiliaryDO convertSkuAuxiliaryDO(SkuAuxiliarySaveReqVO saveReqVO);
}