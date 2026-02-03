package com.syj.eplus.module.wms.convert.bill;

import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface BillConvert {

        BillConvert INSTANCE = Mappers.getMapper(BillConvert.class);

        BillRespVO convert(BillDO billDO);

        default BillRespVO convertBillRespVO(BillDO billDO){
                BillRespVO billRespVO = convert(billDO);
                return billRespVO;
        }

    BillDO convertBillDO(BillSaveReqVO saveReqVO);
}
