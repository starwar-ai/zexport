package com.syj.eplus.module.wms.convert.stock;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.enums.StockMethodEnum;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.StockRespVO;
import com.syj.eplus.module.wms.controller.admin.stock.vo.StockSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface StockConvert {

    StockConvert INSTANCE = Mappers.getMapper(StockConvert.class);

    StockRespVO convert(StockDO stockDO);

    default StockRespVO convertStockRespVO(StockDO stockDO) {
        StockRespVO stockRespVO = convert(stockDO);
        return stockRespVO;
    }

    StockDO convertStockDO(StockSaveReqVO saveReqVO);

    @Mapping(target = "saleContractCode",source = "saleContractCode")
    @Mapping(target = "saleContractId",source = "saleContractId")
    StockDTO convertStockDTO(StockDO stockDO);
    default List<StockDTO> convertStockDTOList(List<StockDO> stockDOList){
        return CollectionUtils.convertList(stockDOList, s->{
            StockDTO stockDTO = convertStockDTO(s);
            stockDTO.setStockMethod(StockMethodEnum.STOCK.getType());
            return stockDTO;
        });
    }
}
