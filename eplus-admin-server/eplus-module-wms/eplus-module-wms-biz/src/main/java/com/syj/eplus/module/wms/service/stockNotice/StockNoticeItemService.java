package com.syj.eplus.module.wms.service.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;

import javax.validation.Valid;

/**
 * 仓储管理-入(出)库通知单-明细 Service 接口
 *
 * @author ePlus
 */
public interface StockNoticeItemService {

    /**
     * 创建仓储管理-入(出)库通知单-明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNoticeItem(@Valid StockNoticeItemSaveReqVO createReqVO);

    /**
     * 更新仓储管理-入(出)库通知单-明细
     *
     * @param updateReqVO 更新信息
     */
    void updateNoticeItem(@Valid StockNoticeItemSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-入(出)库通知单-明细
     *
     * @param id 编号
     */
    void deleteNoticeItem(Long id);

    /**
     * 获得仓储管理-入(出)库通知单-明细
     *
     * @param id 编号
     * @return 仓储管理-入(出)库通知单-明细
     */
    StockNoticeItemRespVO getNoticeItem(Long id);

    /**
     * 获得仓储管理-入(出)库通知单-明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-入(出)库通知单-明细分页
     */
    PageResult<StockNoticeItemDO> getNoticeItemPage(StockNoticeItemPageReqVO pageReqVO);
}
