package com.syj.eplus.module.wms.dal.mysql.stocktake;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakePageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-盘点单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StocktakeMapper extends BaseMapperX<StocktakeDO> {

    default PageResult<StocktakeDO> selectPage(StocktakePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StocktakeDO>()
                .eqIfPresent(StocktakeDO::getCode, reqVO.getCode())
                .eqIfPresent(StocktakeDO::getStocktakeStatus, reqVO.getStocktakeStatus())
                .betweenIfPresent(StocktakeDO::getActStartTime, reqVO.getActStartTime())
                .betweenIfPresent(StocktakeDO::getActEndTime, reqVO.getActEndTime())
                .eqIfPresent(StocktakeDO::getPurchaseContractId, reqVO.getPurchaseContractId())
                .eqIfPresent(StocktakeDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(StocktakeDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(StocktakeDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(StocktakeDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(StocktakeDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(StocktakeDO::getStocktakeUserId, reqVO.getStocktakeUserId())
                .likeIfPresent(StocktakeDO::getStocktakeUserName, reqVO.getStocktakeUserName())
                .eqIfPresent(StocktakeDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(StocktakeDO::getCompanyName, reqVO.getCompanyName())
                .inIfPresent(StocktakeDO::getId, reqVO.getIdList())
                .orderByDesc(StocktakeDO::getId));
    }

}
