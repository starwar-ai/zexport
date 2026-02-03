package com.syj.eplus.module.wms.convert.stocklock;

import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface StockLockConvert {

    StockLockConvert INSTANCE = Mappers.getMapper(StockLockConvert.class);

    StockLockRespVO convert(StockLockDO stockLockDO);

    default StockLockRespVO convertStockLockRespVO(StockLockDO stockLockDO) {
        StockLockRespVO stockLockRespVO = convert(stockLockDO);
        return stockLockRespVO;
    }

    StockLockDO convertStockLockDO(StockLockSaveReqVO saveReqVO);

    List<StockLockDO> convertStockLockDOList(List<StockLockSaveReqVO> saveReqVOList);
}
