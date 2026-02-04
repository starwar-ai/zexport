package com.syj.eplus.module.wms.convert.adjustment;

import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface AdjustmentConvert {

        AdjustmentConvert INSTANCE = Mappers.getMapper(AdjustmentConvert.class);

        AdjustmentRespVO convert(AdjustmentDO adjustmentDO);

        default AdjustmentRespVO convertAdjustmentRespVO(AdjustmentDO adjustmentDO){
                AdjustmentRespVO adjustmentRespVO = convert(adjustmentDO);
                return adjustmentRespVO;
        }

    AdjustmentDO convertAdjustmentDO(AdjustmentSaveReqVO saveReqVO);
}
