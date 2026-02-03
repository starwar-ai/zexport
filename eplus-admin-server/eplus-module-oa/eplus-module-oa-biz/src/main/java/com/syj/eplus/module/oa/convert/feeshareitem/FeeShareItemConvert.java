package com.syj.eplus.module.oa.convert.feeshareitem;

import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemRespVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeShareItemConvert {

        FeeShareItemConvert INSTANCE = Mappers.getMapper(FeeShareItemConvert.class);

        FeeShareItemRespVO convert(FeeShareItemDO feeShareItemDO);

        default FeeShareItemRespVO convertFeeShareItemRespVO(FeeShareItemDO feeShareItemDO){
                FeeShareItemRespVO feeShareItemRespVO = convert(feeShareItemDO);
                return feeShareItemRespVO;
        }

    FeeShareItemDO convertFeeShareItemDO(FeeShareItemSaveReqVO saveReqVO);
}