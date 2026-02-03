package com.syj.eplus.module.wms.convert.stocktake;

import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StocktakeConvert {

        StocktakeConvert INSTANCE = Mappers.getMapper(StocktakeConvert.class);

        StocktakeRespVO convert(StocktakeDO stocktakeDO);

        default StocktakeRespVO convertStocktakeRespVO(StocktakeDO stocktakeDO){
                StocktakeRespVO stocktakeRespVO = convert(stocktakeDO);
                return stocktakeRespVO;
        }

    StocktakeDO convertStocktakeDO(StocktakeSaveReqVO saveReqVO);
}
