package com.syj.eplus.module.dms.dal.mysql.shipmentitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.ShippingStatusEnum;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 出运单明细 Mapper
 *
 * @author du
 */
@Mapper
public interface ShipmentItemMapper extends BaseMapperX<ShipmentItem> {
    default Map<Long, List<ShipmentItem>> getShipmentItemMapByShipmentIdList(List<Long> shipmentIdList) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getShipmentId, shipmentIdList);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
    }

    default List<ShipmentItem> getShipmentItemListByCustCode(String custCode) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>();
        queryWrapperX.eq(ShipmentItem::getCustCode, custCode);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    default List<ShipmentItem> getShipmentItemListByVenderCode(String venderCode) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>();
        queryWrapperX.eq(ShipmentItem::getVenderCode, venderCode);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    default List<ShipmentItem> getShipmentItemListBySaleContractCode(String saleContractCode) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>();
        queryWrapperX.eq(ShipmentItem::getSaleContractCode, saleContractCode);
        queryWrapperX.notIn(ShipmentItem::getStatus, List.of(ShippingStatusEnum.CASE_CLOSED.getValue(),ShippingStatusEnum.COMPLETED.getValue()));
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    default List<ShipmentItem> getShipmentItemListBySkuCodeList(List<String> skuCodeList) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>();
        queryWrapperX.in(ShipmentItem::getSkuCode, skuCodeList);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    default List<ShipmentItem> getShipmentItemListByShipmentIdList(List<Long> shipmentIdList) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getShipmentId, shipmentIdList);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    default List<ShipmentItem> getShipmentItemListByIdList(List<Long> idList) {
        LambdaQueryWrapperX<ShipmentItem> queryWrapperX = new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getId, idList);
        List<ShipmentItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList;
    }

    @Select("""
              SELECT DISTINCT s.STATUS
              FROM dms_shipment_item s
              WHERE s.shipment_id = (SELECT shipment_id FROM dms_shipment_item WHERE id = #{itemId})
                AND s.deleted = 0
            """)
    List<Integer> getShipmentItemStatusLink(@Param("itemId") Long itemId);

    @ResultMap(value = "BaseResultMap")
    @Select("""
            SELECT\s
            a.sale_contract_item_id AS saleContractItemId,
            SUM(a.shipping_quantity) AS shippingQuantity
            FROM `dms_shipment_item` a
            left JOIN dms_shipment b
            on a.shipment_id = b.id
            where b.deleted = 0 and a.deleted = 0 and b.`status` <> 6 and a.sale_contract_code in (select distinct sale_contract_code from dms_shipment_item t where t.shipment_id = (select id from dms_shipment x where x.code = #{shipmentCode}))\s
            GROUP BY
            a.sale_contract_item_id
            """)
    List<ShipmentItem> getShippingQuantity(@Param("shipmentCode")String shipmentCode);
}
