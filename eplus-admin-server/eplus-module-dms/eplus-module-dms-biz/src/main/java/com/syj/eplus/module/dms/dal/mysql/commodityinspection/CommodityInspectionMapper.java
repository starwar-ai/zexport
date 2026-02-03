package com.syj.eplus.module.dms.dal.mysql.commodityinspection;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspection.CommodityInspectionDO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商检单 Mapper
 *
 * @author du
 */
@Mapper
public interface CommodityInspectionMapper extends BaseMapperX<CommodityInspectionDO> {

    default PageResult<CommodityInspectionDO> selectPage(CommodityInspectionPageReqVO reqVO) {
        MPJLambdaWrapperX<CommodityInspectionDO> queryWrapperX = new MPJLambdaWrapperX<CommodityInspectionDO>()
                .selectAll(CommodityInspectionDO.class)
                .eqIfPresent(CommodityInspectionDO::getForeignTradeCompanyId, reqVO.getForeignTradeCompanyId())
                .likeIfPresent(CommodityInspectionDO::getCode, reqVO.getCode())
                .likeIfPresent(CommodityInspectionDO::getInvoiceCode, reqVO.getInvoiceCode())
                .eqIfPresent(CommodityInspectionDO::getShipmentPlanCode, reqVO.getShipmentPlanCode())
                .betweenIfPresent(CommodityInspectionDO::getCreateTime, reqVO.getCreateTime())
                .innerJoin(CommodityInspectionItem.class, CommodityInspectionItem::getCommodityInspectionId, CommodityInspectionDO::getId);
 
        if (StrUtil.isNotEmpty(reqVO.getCustName())) {
            queryWrapperX.like(CommodityInspectionItem::getCustName, reqVO.getCustName());
        }
        if (StrUtil.isNotEmpty(reqVO.getVenderName())) {
            queryWrapperX.like(CommodityInspectionItem::getVenderName, reqVO.getVenderName());
        }
 
        queryWrapperX.groupBy(CommodityInspectionDO::getId)
                .orderByDesc(CommodityInspectionDO::getId);

        return selectJoinPage(reqVO, CommodityInspectionDO.class, queryWrapperX);
    }

}