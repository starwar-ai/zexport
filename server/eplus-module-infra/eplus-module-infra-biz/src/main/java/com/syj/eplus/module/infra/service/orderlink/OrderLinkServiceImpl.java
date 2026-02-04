package com.syj.eplus.module.infra.service.orderlink;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.infra.converter.OrderLinkConvert;
import com.syj.eplus.module.infra.dal.dataobject.orderlink.OrderLink;
import com.syj.eplus.module.infra.dal.mysql.orderlink.OrderLinkMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class OrderLinkServiceImpl implements OrderLinkService {

    @Resource
    private OrderLinkMapper orderLinkMapper;

    @Resource
    private CompanyApi companyApi;
    @Override
    public Long createOrderLink(OrderLinkDTO orderLinkDTO) {
        OrderLink orderLink = OrderLinkConvert.INSTANCE.convert(orderLinkDTO);
        orderLinkMapper.insert(orderLink);
        return orderLink.getId();
    }

    @Override
    public void batchCreateOrderLink(List<OrderLinkDTO> orderLinkDTOList) {
        if (CollUtil.isEmpty(orderLinkDTOList)) {
            return;
        }
        List<OrderLink> orderLinkList = OrderLinkConvert.INSTANCE.convertList(orderLinkDTOList);
        orderLinkMapper.insertBatch(orderLinkList);
    }

    @Override
    public void updateOrderLinkStatus(String code, String name, List<String> linkCodes, Integer status) {
        LambdaQueryWrapperX<OrderLink> queryWrapperX = new LambdaQueryWrapperX<OrderLink>().eq(OrderLink::getCode, code).eq(OrderLink::getName, name).in(OrderLink::getLinkCode, linkCodes);
        orderLinkMapper.update(new OrderLink().setStatus(status), queryWrapperX);
    }

    @Override
    public void batchUpdateOrderLinkStatus(List<OrderLinkDTO> orderLinkDTOList, Integer status) {
        if (CollUtil.isEmpty(orderLinkDTOList)) {
            return;
        }
        // 优化：使用批量更新替代forEach中的N次单条更新
        // 收集所有需要更新的记录ID
        List<Long> ids = orderLinkDTOList.stream()
                .map(OrderLinkDTO::getId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(ids)) {
            // 使用IN条件批量更新
            orderLinkMapper.update(
                    new OrderLink().setStatus(status),
                    new LambdaQueryWrapperX<OrderLink>().in(OrderLink::getId, ids)
            );
        }
    }

    @Override
    public List<OrderLinkDTO> getOrderLinkDTO(List<String> linkCodes) {
        List<OrderLink> orderLinkList = orderLinkMapper.selectList(OrderLink::getLinkCode, linkCodes);
        if (CollUtil.isEmpty(orderLinkList)){
            return List.of();
        }
        List<OrderLinkDTO> orderLinkDTOS = OrderLinkConvert.INSTANCE.convertDTOByDO(orderLinkList);
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isNotEmpty(simpleCompanyDTOMap)){
            orderLinkDTOS.forEach(s->{
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(s.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)){
                    s.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            });
        }
//        return TreeUtil.makeTree(orderLinkDTOS, s -> CommonDict.HYPHEN.equals(s.getLinkCode()), (l, n) -> l.getCode().equals(n.getParentCode()), OrderLinkDTO::setChildren);

        return convertTree(orderLinkDTOS, CommonDict.HYPHEN,null);
    }

    @Override
    public boolean validateOrderLinkExists(List<String> uniqueCodes) {
        if (CollUtil.isEmpty(uniqueCodes)) {
            return false;
        }
        // 使用参数化查询防止SQL注入
        return orderLinkMapper.existsBySourceUniqueCodes(uniqueCodes);
    }

    @Override
    public List<OrderLinkDTO> getOrderLinkDTOByCodeAndName(String code, String name,String linkCode,Integer orderType) {
        LambdaQueryWrapperX<OrderLink> queryWrapperX = new LambdaQueryWrapperX<OrderLink>().eq(OrderLink::getName, name).eq(OrderLink::getLinkCode, linkCode);
        if (BooleanEnum.YES.getValue().equals(orderType)){
            queryWrapperX.eq(OrderLink::getCode,code);
        }else {
            queryWrapperX.eq(OrderLink::getParentCode,code);
        }
        List<OrderLink> orderLinkList = orderLinkMapper.selectList(queryWrapperX);
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isNotEmpty(orderLinkList)){
            orderLinkList.forEach(s->{
                if (Objects.nonNull(s.getCompanyId())){
                    SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(s.getCompanyId());
                    if (Objects.nonNull(simpleCompanyDTO)){
                        s.setCompanyName(simpleCompanyDTO.getCompanyName());
                    }
                }
            });
        }
        return OrderLinkConvert.INSTANCE.convertDTOByDO(orderLinkList);
    }

    @Override
    public List<OrderLinkDTO> getOrderLinkListByLinkCode(List<String> linkCodeList) {
        if (CollUtil.isEmpty(linkCodeList)){
            return List.of();
        }
        List<OrderLink> orderLinkList = orderLinkMapper.selectList(OrderLink::getLinkCode, linkCodeList);
        if (CollUtil.isEmpty(orderLinkList)){
            return List.of();
        }
        orderLinkList = orderLinkList.stream().filter(s->{
            if (BusinessNameDict.PAYMENT_ORDER.equals(s.getName())){
                return !PaymentOrderStatusEnum.CLOSE.getStatus().equals(s.getStatus());
            }else if (BusinessNameDict.PAYMENT_APPLY_ORDER.equals(s.getName())){
                return !BpmProcessInstanceResultEnum.CLOSE.getResult().equals(s.getStatus());
            }else if (BusinessNameDict.EXPORT_SALE_CONTRACT_NAME.equals(s.getName())){
                return !SaleContractStatusEnum.CASE_CLOSED.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.INVOICING_NOTICES.equals(s.getName())){
                return !BpmProcessInstanceResultEnum.CLOSE.getResult().equals(s.getStatus());
            }else if (BusinessNameDict.PURCHASE_CONTRACT_NAME.equals(s.getName())){
                return !PurchaseContractStatusEnum.CASE_SETTLED.getCode().equals(s.getStatus());
            }else if (BusinessNameDict.PURCHASE_PLAN_NAME.equals(s.getName())){
                return !PurchasePlanStatusEnum.CASE_CLOSED.getCode().equals(s.getStatus());
            }else if (BusinessNameDict.SHIPMENT_PLAN_NAME.equals(s.getName())){
                return !ShippingPlanStatusEnum.CASE_CLOSED.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.SHIPMENT_NAME.equals(s.getName())){
                return !ShippingStatusEnum.CASE_CLOSED.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.QUALITY_INSPECTION_NAME.equals(s.getName())){
                return !InspectionBillStatusEnum.CASE_SETTLED.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.SETTLEMENT_NAME.equals(s.getName())){
                return !BooleanEnum.NO.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.COMMODITY_INSPECTION_NAME.equals(s.getName())){
                return !BooleanEnum.NO.getValue().equals(s.getStatus());
            }else if (BusinessNameDict.DECLARATION_NAME.equals(s.getName())){
                return !BooleanEnum.NO.getValue().equals(s.getStatus());
            }else {
                return true;
            }
        }).toList();
        if (CollUtil.isEmpty(orderLinkList)){
            return List.of();
        }
        List<OrderLinkDTO> orderLinkDTOS = OrderLinkConvert.INSTANCE.convertDTOByDO(orderLinkList);
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
        if (CollUtil.isNotEmpty(simpleCompanyDTOMap)){
            orderLinkDTOS.forEach(s->{
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(s.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)){
                    s.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            });
        }
        return orderLinkDTOS.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                s -> s.getCode() + CommonDict.BASE_SNAKE + s.getName(), // 使用code和name的组合作为key
                                Function.identity(),
                                (existing, replacement) -> existing
                        ),
                        map -> new ArrayList<>(map.values())
                )).stream()
                .sorted(Comparator.comparing(OrderLinkDTO::getType))
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderLink(OrderLinkDTO orderLinkDTO) {
        LambdaQueryWrapperX<OrderLink> queryWrapperX = new LambdaQueryWrapperX<OrderLink>().eq(OrderLink::getCode, orderLinkDTO.getCode()).eq(OrderLink::getName, orderLinkDTO.getName()).eq(OrderLink::getType, orderLinkDTO.getType());
        List<OrderLink> orderLinkList = orderLinkMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(orderLinkList)){
            return;
        }
        orderLinkList.forEach(s->{
            s.setOrderMsg(orderLinkDTO.getOrderMsg());
            s.setItem(orderLinkDTO.getItem());
            if (StrUtil.isNotEmpty(orderLinkDTO.getNewCode())){
                s.setCode(orderLinkDTO.getNewCode());
            }
        });

        orderLinkMapper.updateBatch(orderLinkList);
    }

    @Override
    public List<OrderLinkDTO> copyOrderLink(Map<String,String> linkCodeMap, Object orderMsg,Integer type,String code,Long id) {
        if (CollUtil.isEmpty(linkCodeMap)||Objects.isNull(type)|| StrUtil.isEmpty(code)){
            return List.of();
        }
        List<OrderLink> orderLinks = orderLinkMapper.selectList(new LambdaQueryWrapperX<OrderLink>().in(OrderLink::getLinkCode, linkCodeMap.keySet()));
        if (CollUtil.isEmpty(orderLinks)){
            return List.of();
        }
        List<OrderLink> createList = orderLinks.stream().filter(s -> s.getType() <= type)
                .map(s -> {
                    String newLinkCode = linkCodeMap.get(s.getLinkCode());
                    if (StrUtil.isEmpty(newLinkCode)) {
                        return null;
                    }
                    s.setId(null);
                    s.setLinkCode(newLinkCode);
                    if (type.equals(s.getType())) {
                        s.setOrderMsg(orderMsg);
                        s.setCode(code);
                    }
                    return s;
                }).filter(Objects::nonNull).toList();
        orderLinkMapper.insertBatch(createList);
        return createList.stream().map(OrderLinkConvert.INSTANCE::convertOrderLinkDTOByDO).collect(Collectors.toList());
    }

    @Override
    public void deleteOrderLink(Integer type, String code) {
        orderLinkMapper.delete(new LambdaQueryWrapperX<OrderLink>().eq(OrderLink::getCode, code).eq(OrderLink::getType, type));
    }

    /**
     * 构建树节点  TODO 待完善 统一使用TreeUtil
     */
    private List<OrderLinkDTO> convertTree(List<OrderLinkDTO> subjectDOS, String head,Integer type) {
        return subjectDOS.stream()
                // 过滤父节点
                .filter(parent -> {
                    if (Objects.nonNull(type)){
                       return Objects.equals(parent.getParentCode(), head)&&type.equals(parent.getType());
                    }else {
                        return Objects.equals(parent.getParentCode(), head);
                    }
                })
                // 把父节点children递归赋值成为子节点
                .map(s -> BeanUtils.toBean(s, OrderLinkDTO.class))
                .peek(child -> child.setChildren(convertTree(subjectDOS, child.getCode(),child.getType()))).toList();
    }
}
