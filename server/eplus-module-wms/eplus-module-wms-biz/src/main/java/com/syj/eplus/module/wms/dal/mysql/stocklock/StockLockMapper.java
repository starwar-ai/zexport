package com.syj.eplus.module.wms.dal.mysql.stocklock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stocklock.vo.StockLockPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-库存明细-锁定库存 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StockLockMapper extends BaseMapperX<StockLockDO> {

    default PageResult<StockLockDO> selectPage(StockLockPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockLockDO>()
                .eqIfPresent(StockLockDO::getStockId, reqVO.getStockId())
                .eqIfPresent(StockLockDO::getSourceOrderType, reqVO.getSourceOrderType())
                .eqIfPresent(StockLockDO::getLockType, reqVO.getLockType())
                .eqIfPresent(StockLockDO::getLockQuantity, reqVO.getLockQuantity())
                .betweenIfPresent(StockLockDO::getLockTime, reqVO.getLockTime())
                .eqIfPresent(StockLockDO::getLockByUserId, reqVO.getLockByUserId())
                .likeIfPresent(StockLockDO::getLockByUserName, reqVO.getLockByUserName())
                .eqIfPresent(StockLockDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(StockLockDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockLockDO::getId));
    }

}
