package com.syj.eplus.module.mms.convert.manufactureskuitem;

import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemRespVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ManufactureSkuItemConvert {

        ManufactureSkuItemConvert INSTANCE = Mappers.getMapper(ManufactureSkuItemConvert.class);

        ManufactureSkuItemRespVO convert(ManufactureSkuItemDO manufactureSkuItemDO);

        default ManufactureSkuItemRespVO convertManufactureSkuItemRespVO(ManufactureSkuItemDO manufactureSkuItemDO){
                ManufactureSkuItemRespVO manufactureSkuItemRespVO = convert(manufactureSkuItemDO);
                return manufactureSkuItemRespVO;
        }

    ManufactureSkuItemDO convertManufactureSkuItemDO(ManufactureSkuItemSaveReqVO saveReqVO);
}