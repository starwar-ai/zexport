package com.syj.eplus.module.dms.dal.mysql.shipmentplanitem;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 出运计划单明细 Mapper
 *
 * @author du
 */
@Mapper
public interface ShipmentPlanItemMapper extends BaseMapperX<ShipmentPlanItem> {

    @Select("""
            SELECT DISTINCT t.STATUS
            FROM dms_shipment_plan p
            JOIN dms_shipment_plan_item t ON p.id = t.shipment_plan_id
            WHERE p.id = (SELECT shipment_plan_id FROM dms_shipment_plan_item WHERE id = #{itemId})
            """)
    List<Integer> getShipmentPlanItemStatusLink(@Param("itemId") Long itemId);
}