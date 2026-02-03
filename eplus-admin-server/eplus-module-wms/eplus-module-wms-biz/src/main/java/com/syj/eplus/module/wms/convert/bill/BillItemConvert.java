package com.syj.eplus.module.wms.convert.bill;

import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface BillItemConvert {

        BillItemConvert INSTANCE = Mappers.getMapper(BillItemConvert.class);

        BillItemRespVO convert(BillItemDO billItemDO);

        default BillItemRespVO convertBillItemRespVO(BillItemDO billItemDO){
                BillItemRespVO billItemRespVO = convert(billItemDO);
                return billItemRespVO;
        }

    BillItemDO convertBillItemDO(BillItemSaveReqVO saveReqVO);

        List<BillItemDO> convertBillItemDOList(List<BillItemRespVO> billItemRespVOList);
}
