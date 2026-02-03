package com.syj.eplus.module.convert.sendbill;

import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillRespVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillSaveReqVO;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SendBillConvert {

        SendBillConvert INSTANCE = Mappers.getMapper(SendBillConvert.class);

        SendBillRespVO convert(SendBillDO sendBillDO);

        default SendBillRespVO convertSendBillRespVO(SendBillDO sendBillDO){
                SendBillRespVO sendBillRespVO = convert(sendBillDO);
                return sendBillRespVO;
        }

    SendBillDO convertSendBillDO(SendBillSaveReqVO saveReqVO);
}