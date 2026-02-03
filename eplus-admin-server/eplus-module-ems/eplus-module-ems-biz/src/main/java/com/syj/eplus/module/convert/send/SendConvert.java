package com.syj.eplus.module.convert.send;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.controller.admin.send.vo.SendInfoRespVO;
import com.syj.eplus.module.controller.admin.send.vo.SendRespVO;
import com.syj.eplus.module.controller.admin.send.vo.SendSaveReqVO;
import com.syj.eplus.module.controller.admin.send.vo.feeshare.FeeShareReqVO;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SendConvert {

        SendConvert INSTANCE = Mappers.getMapper(SendConvert.class);

        SendRespVO convert(SendDO sendDO);

        default SendInfoRespVO convertSendRespVO(SendDO sendDO){
                SendInfoRespVO sendRespVO = BeanUtils.toBean(sendDO,SendInfoRespVO.class);
                return sendRespVO;
        }

    SendDO convertSendDO(SendSaveReqVO saveReqVO);

        FeeShareReqDTO convertFeeShareDTOItem(FeeShareReqVO shareRespVO);
}