package com.syj.eplus.module.wms.dal.mysql.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-盘库调整单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface AdjustmentMapper extends BaseMapperX<AdjustmentDO> {

    default PageResult<AdjustmentDO> selectPage(AdjustmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdjustmentDO>()
                .eqIfPresent(AdjustmentDO::getCode, reqVO.getCode())
                .eqIfPresent(AdjustmentDO::getStocktakeId, reqVO.getStocktakeId())
                .eqIfPresent(AdjustmentDO::getAdjustmentType, reqVO.getAdjustmentType())
                .eqIfPresent(AdjustmentDO::getPurchaseContractId, reqVO.getPurchaseContractId())
                .eqIfPresent(AdjustmentDO::getPurchaseContractCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(AdjustmentDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(AdjustmentDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(AdjustmentDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(AdjustmentDO::getWarehouseName, reqVO.getWarehouseName())
                .betweenIfPresent(AdjustmentDO::getAdjustmentDate, reqVO.getAdjustmentDate())
                .eqIfPresent(AdjustmentDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(AdjustmentDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(AdjustmentDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(AdjustmentDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(AdjustmentDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(AdjustmentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdjustmentDO::getId));
    }

}
