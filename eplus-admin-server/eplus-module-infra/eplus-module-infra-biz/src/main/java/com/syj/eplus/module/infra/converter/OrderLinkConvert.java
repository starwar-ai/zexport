package com.syj.eplus.module.infra.converter;

import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.infra.dal.dataobject.orderlink.OrderLink;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderLinkConvert {
    OrderLinkConvert INSTANCE = Mappers.getMapper(OrderLinkConvert.class);

    OrderLink convert(OrderLinkDTO orderLinkDTO);

    List<OrderLinkDTO> convertDTOByDO(List<OrderLink> orderLink);
    List<OrderLink> convertList(List<OrderLinkDTO> orderLink);
    OrderLinkDTO convertOrderLinkDTOByDO(OrderLink orderLink);
}
