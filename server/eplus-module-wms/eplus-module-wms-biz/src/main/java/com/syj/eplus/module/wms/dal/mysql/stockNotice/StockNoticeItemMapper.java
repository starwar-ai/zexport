package com.syj.eplus.module.wms.dal.mysql.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemPageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-入(出)库通知单-明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StockNoticeItemMapper extends BaseMapperX<StockNoticeItemDO> {

    default PageResult<StockNoticeItemDO> selectPage(StockNoticeItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockNoticeItemDO>()
                .eqIfPresent(StockNoticeItemDO::getNoticeId, reqVO.getNoticeId())
                .eqIfPresent(StockNoticeItemDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(StockNoticeItemDO::getSourceSortNum, reqVO.getSourceSortNum())
                .eqIfPresent(StockNoticeItemDO::getNoticeItemStatus, reqVO.getNoticeItemStatus())
                .eqIfPresent(StockNoticeItemDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(StockNoticeItemDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(StockNoticeItemDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(StockNoticeItemDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(StockNoticeItemDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(StockNoticeItemDO::getVenderId, reqVO.getVenderId())
                .likeIfPresent(StockNoticeItemDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(StockNoticeItemDO::getCustId, reqVO.getCustId())
                .likeIfPresent(StockNoticeItemDO::getCustName, reqVO.getCustName())
                .eqIfPresent(StockNoticeItemDO::getCustPo, reqVO.getCustPo())
                .eqIfPresent(StockNoticeItemDO::getOrderQuantity, reqVO.getOrderQuantity())
                .eqIfPresent(StockNoticeItemDO::getOrderBoxQuantity, reqVO.getOrderBoxQuantity())
                .eqIfPresent(StockNoticeItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(StockNoticeItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(StockNoticeItemDO::getTotalVolume, reqVO.getTotalVolume())
                .eqIfPresent(StockNoticeItemDO::getTotalWeight, reqVO.getTotalWeight())
                .eqIfPresent(StockNoticeItemDO::getPalletVolume, reqVO.getPalletVolume())
                .eqIfPresent(StockNoticeItemDO::getPalletWeight, reqVO.getPalletWeight())
                .eqIfPresent(StockNoticeItemDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(StockNoticeItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockNoticeItemDO::getId));
    }

}
