package com.syj.eplus.module.wms.dal.mysql.stocktake;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-盘点单-明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StocktakeItemMapper extends BaseMapperX<StocktakeItemDO> {

    default PageResult<StocktakeItemDO> selectPage(StocktakeItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StocktakeItemDO>()
                .eqIfPresent(StocktakeItemDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(StocktakeItemDO::getStocktakeId, reqVO.getStocktakeId())
                .eqIfPresent(StocktakeItemDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(StocktakeItemDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StocktakeItemDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(StocktakeItemDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(StocktakeItemDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(StocktakeItemDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(StocktakeItemDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(StocktakeItemDO::getPosition, reqVO.getPosition())
                .eqIfPresent(StocktakeItemDO::getStockQuantity, reqVO.getStockQuantity())
                .eqIfPresent(StocktakeItemDO::getStockBoxQuantity, reqVO.getStockBoxQuantity())
                .eqIfPresent(StocktakeItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(StocktakeItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(StocktakeItemDO::getStocktakePosition, reqVO.getStocktakePosition())
                .eqIfPresent(StocktakeItemDO::getStocktakeStockQuantity, reqVO.getStocktakeStockQuantity())
                .eqIfPresent(StocktakeItemDO::getStocktakeStockBoxQuantity, reqVO.getStocktakeStockBoxQuantity())
                .eqIfPresent(StocktakeItemDO::getVenderId, reqVO.getVenderId())
                .likeIfPresent(StocktakeItemDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(StocktakeItemDO::getCustId, reqVO.getCustId())
                .likeIfPresent(StocktakeItemDO::getCustName, reqVO.getCustName())
                .eqIfPresent(StocktakeItemDO::getCustPo, reqVO.getCustPo())
                .eqIfPresent(StocktakeItemDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(StocktakeItemDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(StocktakeItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(StocktakeItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StocktakeItemDO::getId));
    }

    default PageResult<StocktakeItemDO> selectPage(PageParam reqVO, Long stocktakeId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StocktakeItemDO>()
                .eq(StocktakeItemDO::getStocktakeId, stocktakeId)
                .orderByDesc(StocktakeItemDO::getId));
    }

    default int deleteByStocktakeId(Long stocktakeId) {
        return delete(StocktakeItemDO::getStocktakeId, stocktakeId);
    }
}
