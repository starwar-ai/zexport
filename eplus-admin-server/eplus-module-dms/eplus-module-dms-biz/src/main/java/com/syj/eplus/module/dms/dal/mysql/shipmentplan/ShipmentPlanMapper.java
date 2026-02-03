package com.syj.eplus.module.dms.dal.mysql.shipmentplan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.ShipmentPlanPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 出运计划单 Mapper
 *
 * @author du
 */
@Mapper
public interface ShipmentPlanMapper extends BaseMapperX<ShipmentPlanDO> {

    default PageResult<ShipmentPlanDO> selectPage(ShipmentPlanPageReqVO reqVO) {
        LambdaQueryWrapperX<ShipmentPlanDO> queryWrapperX = new LambdaQueryWrapperX<ShipmentPlanDO>()
                .likeIfPresent(ShipmentPlanDO::getCode, reqVO.getCode())
                .eqIfPresent(ShipmentPlanDO::getStatus, reqVO.getStatus())
                .neIfPresent(ShipmentPlanDO::getStatus, reqVO.getNeStatus())
                .likeIfPresent(ShipmentPlanDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(ShipmentPlanDO::getForeignTradeCompanyId, reqVO.getForeignTradeCompanyId())
                .orderByDesc(ShipmentPlanDO::getId);
        if (CollUtil.isNotEmpty(reqVO.getIdList())){
            queryWrapperX.in(ShipmentPlanDO::getId, reqVO.getIdList());
        }
        return selectPage(reqVO,queryWrapperX);
    }

    @Select("""
            SELECT DISTINCT t.STATUS
            FROM dms_shipment_plan p
            JOIN dms_shipment_plan_item t ON p.id = t.shipment_plan_id
            WHERE p.id = #{id} and t.deleted = 0
            """)
    List<Integer> getShipmentPlanItemStatus(@Param("id") Long id);

    @Select("""
            select count(1) from dms_shipment_plan a where a.id =#{id} and  a.deleted=0 and exists( \s
            	select 1 from dms_shipment b where a.code = b.shipment_plan_code and b.deleted = 0 and b.status <> 6 \s
            )
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);
}
