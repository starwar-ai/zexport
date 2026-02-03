package com.syj.eplus.module.mms.convert.manufacturesku;

import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuRespVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ManufactureSkuConvert {

        ManufactureSkuConvert INSTANCE = Mappers.getMapper(ManufactureSkuConvert.class);

        ManufactureSkuRespVO convert(ManufactureSkuDO manufactureSkuDO);

        default ManufactureSkuRespVO convertManufactureSkuRespVO(ManufactureSkuDO manufactureSkuDO){
                ManufactureSkuRespVO manufactureSkuRespVO = convert(manufactureSkuDO);
                return manufactureSkuRespVO;
        }

    ManufactureSkuDO convertManufactureSkuDO(ManufactureSkuSaveReqVO saveReqVO);
}