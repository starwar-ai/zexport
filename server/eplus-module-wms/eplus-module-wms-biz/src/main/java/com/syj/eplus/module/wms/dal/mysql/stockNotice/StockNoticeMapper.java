package com.syj.eplus.module.wms.dal.mysql.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓储管理-入(出)库通知单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface StockNoticeMapper extends BaseMapperX<StockNoticeDO> {

    default PageResult<StockNoticeDO> selectPage(StockNoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockNoticeDO>()
                // 通知单号
                .likeIfPresent(StockNoticeDO::getCode, reqVO.getCode())
                // 通知时间
                .betweenIfPresent(StockNoticeDO::getNoticeTime, reqVO.getNoticeTime())
                // 期望到/出货时间
                .betweenIfPresent(StockNoticeDO::getExpectDate, reqVO.getExpectDate())
                .eqIfPresent(StockNoticeDO::getNoticeType, reqVO.getNoticeType())
                .neIfPresent(StockNoticeDO::getNoticeStatus,reqVO.getNotInNoticeStatus())
                .eqIfPresent(StockNoticeDO::getNoticeStatus, reqVO.getNoticeStatus())
                .likeIfPresent(StockNoticeDO::getInvoiceCode,reqVO.getInvoiceCode())
                .eqIfPresent(StockNoticeDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(StockNoticeDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(StockNoticeDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(StockNoticeDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(StockNoticeDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(StockNoticeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(StockNoticeDO::getIsContainerTransportation, reqVO.getIsContainerTransportation())
                .inIfPresent(StockNoticeDO::getId, reqVO.getIdList())
                .orderByDesc(StockNoticeDO::getId));
    }

}
