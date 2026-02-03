package com.syj.eplus.module.wms.dal.mysql.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-入(出)库单-明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface BillItemMapper extends BaseMapperX<BillItemDO> {

    default PageResult<BillItemDO> selectPage(BillItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BillItemDO>()
                .eqIfPresent(BillItemDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(BillItemDO::getSourceId, reqVO.getSourceId())
                .eqIfPresent(BillItemDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(BillItemDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(BillItemDO::getSourceSortNum, reqVO.getSourceSortNum())
                .eqIfPresent(BillItemDO::getNoticeItemStatus, reqVO.getNoticeItemStatus())
                .eqIfPresent(BillItemDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(BillItemDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(BillItemDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(BillItemDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(BillItemDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(BillItemDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(BillItemDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(BillItemDO::getPosition, reqVO.getPosition())
                .eqIfPresent(BillItemDO::getVenderId, reqVO.getVenderId())
                .likeIfPresent(BillItemDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(BillItemDO::getCustId, reqVO.getCustId())
                .likeIfPresent(BillItemDO::getCustName, reqVO.getCustName())
                .eqIfPresent(BillItemDO::getCustPo, reqVO.getCustPo())
                .eqIfPresent(BillItemDO::getOrderQuantity, reqVO.getOrderQuantity())
                .eqIfPresent(BillItemDO::getOrderBoxQuantity, reqVO.getOrderBoxQuantity())
                .eqIfPresent(BillItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(BillItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(BillItemDO::getActQuantity, reqVO.getActQuantity())
                .eqIfPresent(BillItemDO::getActBoxQuantity, reqVO.getActBoxQuantity())
                .eqIfPresent(BillItemDO::getTotalVolume, reqVO.getTotalVolume())
                .eqIfPresent(BillItemDO::getTotalWeight, reqVO.getTotalWeight())
                .eqIfPresent(BillItemDO::getPalletVolume, reqVO.getPalletVolume())
                .eqIfPresent(BillItemDO::getPalletWeight, reqVO.getPalletWeight())
                .eqIfPresent(BillItemDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(BillItemDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(BillItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(BillItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BillItemDO::getId));
    }
}
