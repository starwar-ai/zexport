package com.syj.eplus.module.dms.dal.mysql.shipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Objects;

/**
 * 出运单 Mapper
 *
 * @author du
 */
@Mapper
public interface ShipmentMapper extends BaseMapperX<ShipmentDO> {

    default PageResult<ShipmentDO> selectPage(ShipmentPageReqVO reqVO) {
        LambdaQueryWrapperX<ShipmentDO> queryWrapperX = new LambdaQueryWrapperX<ShipmentDO>()
                .eqIfPresent(ShipmentDO::getCode, reqVO.getCode())
                .neIfPresent(ShipmentDO::getId, reqVO.getExceptId())
                .eqIfPresent(ShipmentDO::getConfirmFlag, reqVO.getConfirmFlag())
                .eqIfPresent(ShipmentDO::getCustContractCode, reqVO.getCustContractCode())
                .eqIfPresent(ShipmentDO::getForeignTradeCompanyId, reqVO.getForeignTradeCompanyId())
                .eqIfPresent(ShipmentDO::getShipmentPlanCode, reqVO.getShipmentPlanCode())
                .eqIfPresent(ShipmentDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ShipmentDO::getInvoiceCode, reqVO.getInvoiceCode())
                .neIfPresent(ShipmentDO::getStatus, reqVO.getNeStatus())
                .neIfPresent(ShipmentDO::getBatchFlag, reqVO.getBatchFlag())
                .inIfPresent(ShipmentDO::getId, reqVO.getIdList())
                .inIfPresent(ShipmentDO::getStatus, reqVO.getStatusList())
                .betweenIfPresent(ShipmentDO::getInputDate, reqVO.getInputDate())
                .orderByDesc(ShipmentDO::getId);
        if (Objects.nonNull(reqVO.getInputUserId())){
            queryWrapperX.eqIfPresent(ShipmentDO::getCreator, String.valueOf(reqVO.getInputUserId()));
        }
        return selectPage(reqVO, queryWrapperX);
    }


    @Select("""
            select count(1) from dms_shipment a where   a.deleted=0 and a.id = #{id} and (exists(\s
            select 1 from dms_commodity_inspection b where a.code = b.shipment_code and b.deleted = 0\s
            )  or exists(
            select 1 from dms_declaration c where a.code = c.shipment_code and c.deleted = 0 \s
            ) or exists(
            select 1 from dms_settlement_form d where a.code = d.shipment_code and d.deleted = 0\s
            ) or exists(
            select 1 from wms_notice e where a.code = e.shipment_code and e.deleted = 0 and e.notice_status !=3\s
            ) or exists (
            select 1 from dms_shipment_change f where a.code = f.code and f.deleted = 0 and f.status <> 2
            )
            )
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);
}