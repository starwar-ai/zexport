package com.syj.eplus.module.infra.api.orderlink;

import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.infra.service.orderlink.OrderLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OrderLinkApiimpl implements OrderLinkApi {

    @Resource
    private OrderLinkService orderLinkService;


    @Override
    public Long createOrderLink(OrderLinkDTO orderLinkDTO) {
        return orderLinkService.createOrderLink(orderLinkDTO);
    }

    @Override
    public void batchCreateOrderLink(List<OrderLinkDTO> orderLinkDTOList) {
        orderLinkService.batchCreateOrderLink(orderLinkDTOList);
    }

    @Override
    public void updateOrderLinkStatus(String code, String name, List<String> linkCodes, Integer status) {
        orderLinkService.updateOrderLinkStatus(code, name, linkCodes, status);
    }

    @Override
    public void batchUpdateOrderLinkStatus(List<OrderLinkDTO> orderLinkDTOList, Integer status) {
        orderLinkService.batchUpdateOrderLinkStatus(orderLinkDTOList, status);
    }

    @Override
    public List<OrderLinkDTO> getOrderLinkDTO(List<String> linkCodes) {
        return orderLinkService.getOrderLinkDTO(linkCodes);
    }

    @Override
    public boolean validateOrderLinkExists(List<String> uniqueCodes) {
        return orderLinkService.validateOrderLinkExists(uniqueCodes);
    }

    @Override
    public void updateOrderLink(OrderLinkDTO orderLinkDTO) {
        orderLinkService.updateOrderLink(orderLinkDTO);
    }

    @Override
    public List<OrderLinkDTO> copyOrderLink(Map<String, String> linkCodeMap, Object orderMsg, Integer type, String code,Long id) {
        return orderLinkService.copyOrderLink(linkCodeMap, orderMsg, type, code,id);
    }

    @Override
    public void deleteOrderLink(Integer type, String code) {
        orderLinkService.deleteOrderLink(type,code);
    }
}
