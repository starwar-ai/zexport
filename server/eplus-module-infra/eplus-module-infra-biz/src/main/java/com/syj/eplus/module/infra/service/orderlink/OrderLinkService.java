package com.syj.eplus.module.infra.service.orderlink;

import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface OrderLinkService {

    /**
     * 创建订单路径记录
     *
     * @param orderLinkDTO 创建信息
     * @return 编号
     */
    Long createOrderLink(@Valid OrderLinkDTO orderLinkDTO);

    /**
     * 批量创建订单流
     *
     * @param orderLinkDTOList 创建信息
     */
    void batchCreateOrderLink(List<OrderLinkDTO> orderLinkDTOList);
    /**
     * 根据链路编号跟订单编号更新订单流中订单状态
     *
     * @param code     订单编号
     * @param name     业务名称
     * @param linkCodes 链路编号
     * @param status   状态
     */
    void updateOrderLinkStatus(String code, String name, List<String> linkCodes, Integer status);

    /**
     * 批量更新订单流状态
     *
     * @param orderLinkDTOList 订单流列表
     * @param status           订单状态
     */
    void batchUpdateOrderLinkStatus(List<OrderLinkDTO> orderLinkDTOList, Integer status);

    /**
     * 根据链路编号获取单据树
     *
     * @param linkCodes 链路编号
     * @return 单据树
     */
    List<OrderLinkDTO> getOrderLinkDTO(List<String> linkCodes);

    /**
     * 校验明细是否有后续单据
     *
     * @param uniqueCodes 明细唯一编号列表
     * @return 是否有后续单据
     */
    boolean validateOrderLinkExists(List<String> uniqueCodes);

    /**
     * 根据父节点编号及模块名称获取链路节点信息
     * @param code 编号
     * @param name 模块名称
     * @param linkCode 链路编号
     * @param orderType 上下游单据类型
     * @return 链路节点信息
     */
    List<OrderLinkDTO> getOrderLinkDTOByCodeAndName(String code,String name,String linkCode,Integer orderType);

    /**
     * 根据链路编号获取链路列表
     * @param linkCodeList  链路编号列表
     * @return 链路列表
     */
    List<OrderLinkDTO> getOrderLinkListByLinkCode(List<String> linkCodeList);

    /**
     * 修改订单流
     * @param orderLinkDTO 订单流信息
     */
    void updateOrderLink(OrderLinkDTO orderLinkDTO);

    /**
     * 复制订单流
     * @param linkCodeMap 链路编号映射
     * @param orderMsg 订单信息
     * @param type 订单类型
     * @param code 模块编号
     * @return 链路编号映射
     */
    List<OrderLinkDTO> copyOrderLink(Map<String,String> linkCodeMap, Object orderMsg,Integer  type,String code,Long id);

    /**
     * 删除订单流
     * @param type 订单类型
     * @param code 模块编号
     */
    void deleteOrderLink(Integer type,String code);
}
