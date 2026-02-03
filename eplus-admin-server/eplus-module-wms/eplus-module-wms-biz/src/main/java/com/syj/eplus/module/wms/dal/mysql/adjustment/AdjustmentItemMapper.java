package com.syj.eplus.module.wms.dal.mysql.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-盘库调整单-明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface AdjustmentItemMapper extends BaseMapperX<AdjustmentItemDO> {

    default PageResult<AdjustmentItemDO> selectPage(AdjustmentItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdjustmentItemDO>()
                .eqIfPresent(AdjustmentItemDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(AdjustmentItemDO::getAdjustmentId, reqVO.getAdjustmentId())
                .eqIfPresent(AdjustmentItemDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(AdjustmentItemDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(AdjustmentItemDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(AdjustmentItemDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(AdjustmentItemDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(AdjustmentItemDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(AdjustmentItemDO::getSourceSortNum, reqVO.getSourceSortNum())
                .eqIfPresent(AdjustmentItemDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(AdjustmentItemDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(AdjustmentItemDO::getPosition, reqVO.getPosition())
                .eqIfPresent(AdjustmentItemDO::getStocktakePosition, reqVO.getStocktakePosition())
                .eqIfPresent(AdjustmentItemDO::getStockQuantity, reqVO.getStockQuantity())
                .eqIfPresent(AdjustmentItemDO::getStocktakeStockQuantity, reqVO.getStocktakeStockQuantity())
                .eqIfPresent(AdjustmentItemDO::getDifferenceQuantity, reqVO.getDifferenceQuantity())
                .eqIfPresent(AdjustmentItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(AdjustmentItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(AdjustmentItemDO::getStockBoxQuantity, reqVO.getStockBoxQuantity())
                .eqIfPresent(AdjustmentItemDO::getStocktakeStockBoxQuantity, reqVO.getStocktakeStockBoxQuantity())
                .eqIfPresent(AdjustmentItemDO::getDifferenceBoxQuantity, reqVO.getDifferenceBoxQuantity())
                .eqIfPresent(AdjustmentItemDO::getVenderId, reqVO.getVenderId())
                .likeIfPresent(AdjustmentItemDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(AdjustmentItemDO::getCustId, reqVO.getCustId())
                .likeIfPresent(AdjustmentItemDO::getCustName, reqVO.getCustName())
                .eqIfPresent(AdjustmentItemDO::getCustPo, reqVO.getCustPo())
                .eqIfPresent(AdjustmentItemDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(AdjustmentItemDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(AdjustmentItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(AdjustmentItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdjustmentItemDO::getId));
    }

    default PageResult<AdjustmentItemDO> selectPage(PageParam reqVO, Long adjustmentId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdjustmentItemDO>()
                .eq(AdjustmentItemDO::getAdjustmentId, adjustmentId)
                .orderByDesc(AdjustmentItemDO::getId));
    }
    default int deleteByAdjustmentId(Long adjustmentId) {
        return delete(AdjustmentItemDO::getAdjustmentId, adjustmentId);
    }
}
