package com.syj.eplus.module.mms.convert.manufacture;

import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureRespVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureSaveInfoReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacture.ManufactureDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ManufactureConvert {

        ManufactureConvert INSTANCE = Mappers.getMapper(ManufactureConvert.class);

        ManufactureRespVO convert(ManufactureDO manufactureDO);

        default ManufactureRespVO convertManufactureRespVO(ManufactureDO manufactureDO){
                ManufactureRespVO manufactureRespVO = convert(manufactureDO);
                return manufactureRespVO;
        }

    ManufactureDO convertManufactureDO(ManufactureSaveInfoReqVO saveReqVO);
}