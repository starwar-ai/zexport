package com.syj.eplus.module.wms.convert.adjustment;

import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface AdjustmentItemConvert {

        AdjustmentItemConvert INSTANCE = Mappers.getMapper(AdjustmentItemConvert.class);

        AdjustmentItemRespVO convert(AdjustmentItemDO adjustmentItemDO);

        default AdjustmentItemRespVO convertAdjustmentItemRespVO(AdjustmentItemDO adjustmentItemDO){
                AdjustmentItemRespVO adjustmentItemRespVO = convert(adjustmentItemDO);
                return adjustmentItemRespVO;
        }

    AdjustmentItemDO convertAdjustmentItemDO(AdjustmentItemSaveReqVO saveReqVO);
}
