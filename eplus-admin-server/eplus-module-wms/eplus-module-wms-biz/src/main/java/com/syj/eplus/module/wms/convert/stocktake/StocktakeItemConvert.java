package com.syj.eplus.module.wms.convert.stocktake;

import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StocktakeItemConvert {

        StocktakeItemConvert INSTANCE = Mappers.getMapper(StocktakeItemConvert.class);

        StocktakeItemRespVO convert(StocktakeItemDO stocktakeItemDO);

        default StocktakeItemRespVO convertStocktakeItemRespVO(StocktakeItemDO stocktakeItemDO){
                StocktakeItemRespVO stocktakeItemRespVO = convert(stocktakeItemDO);
                return stocktakeItemRespVO;
        }

    StocktakeItemDO convertStocktakeItemDO(StocktakeItemSaveReqVO saveReqVO);
}
