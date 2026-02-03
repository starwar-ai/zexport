package com.syj.eplus.module.dms.dal.mysql.commodityinspectionitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商检单明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface CommodityInspectionItemMapper extends BaseMapperX<CommodityInspectionItem> {

    default Map<Long, List<CommodityInspectionItem>> getCommoditInspectionItemMapByCommoditInspectionIdList(List<Long> commodityInspectionIdList) {
        LambdaQueryWrapperX<CommodityInspectionItem> queryWrapperX = new LambdaQueryWrapperX<CommodityInspectionItem>().in(CommodityInspectionItem::getCommodityInspectionId, commodityInspectionIdList);
        List<CommodityInspectionItem> shipmentItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(shipmentItemList)) {
            return null;
        }
        return shipmentItemList.stream().collect(Collectors.groupingBy(CommodityInspectionItem::getCommodityInspectionId));
    }

}