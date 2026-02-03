package com.syj.eplus.module.wms.convert.stockNotice;

import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StockNoticeItemConvert {

        StockNoticeItemConvert INSTANCE = Mappers.getMapper(StockNoticeItemConvert.class);

        StockNoticeItemRespVO convert(StockNoticeItemDO stockNoticeItemDO);

        default StockNoticeItemRespVO convertNoticeItemRespVO(StockNoticeItemDO stockNoticeItemDO){
                StockNoticeItemRespVO stockNoticeItemRespVO = convert(stockNoticeItemDO);
                return stockNoticeItemRespVO;
        }

    StockNoticeItemDO convertNoticeItemDO(StockNoticeItemSaveReqVO saveReqVO);
}
