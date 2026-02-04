package cn.iocoder.yudao.module.system.convert.port;

import cn.iocoder.yudao.module.system.api.port.dto.PortDTO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortRespVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.port.PortDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface PortConvert {

    PortConvert INSTANCE = Mappers.getMapper(PortConvert.class);

    PortRespVO convert(PortDO portDO);

    List<PortDTO> convert(List<PortDO> portDO);

    default PortRespVO convertPortRespVO(PortDO portDO) {
        PortRespVO portRespVO = convert(portDO);
        return portRespVO;
    }

    PortDO convertPortDO(PortSaveReqVO saveReqVO);
}