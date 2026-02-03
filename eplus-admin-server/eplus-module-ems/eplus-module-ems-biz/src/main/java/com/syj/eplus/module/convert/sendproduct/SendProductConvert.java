package com.syj.eplus.module.convert.sendproduct;

import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SendProductConvert {

        SendProductConvert INSTANCE = Mappers.getMapper(SendProductConvert.class);

        SendProductRespVO convert(SendProductDO sendProductDO);

        default SendProductRespVO convertSendProductRespVO(SendProductDO sendProductDO){
                SendProductRespVO sendProductRespVO = convert(sendProductDO);
                return sendProductRespVO;
        }

    SendProductDO convertSendProductDO(SendProductSaveReqVO saveReqVO);
}