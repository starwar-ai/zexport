package com.syj.eplus.module.wms.convert.stockimport;

import com.syj.eplus.module.wms.controller.admin.stockimport.vo.StockImportRespVO;
import com.syj.eplus.module.wms.controller.admin.stockimport.vo.StockImportSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StockImportConvert {

        StockImportConvert INSTANCE = Mappers.getMapper(StockImportConvert.class);

        StockImportRespVO convert(StockImportDO stockImportDO);

        default StockImportRespVO convertStockImportRespVO(StockImportDO stockImportDO){
                StockImportRespVO stockImportRespVO = convert(stockImportDO);
                return stockImportRespVO;
        }

    StockImportDO convertStockImportDO(StockImportSaveReqVO saveReqVO);
}