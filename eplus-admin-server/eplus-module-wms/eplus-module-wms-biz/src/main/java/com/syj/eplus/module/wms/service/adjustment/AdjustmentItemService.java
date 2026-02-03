package com.syj.eplus.module.wms.service.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;

import javax.validation.Valid;

/**
 * 仓储管理-盘库调整单-明细 Service 接口
 *
 * @author ePlus
 */
public interface AdjustmentItemService {

    /**
     * 创建仓储管理-盘库调整单-明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAdjustmentItem(@Valid AdjustmentItemSaveReqVO createReqVO);

    /**
     * 更新仓储管理-盘库调整单-明细
     *
     * @param updateReqVO 更新信息
     */
    void updateAdjustmentItem(@Valid AdjustmentItemSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-盘库调整单-明细
     *
     * @param id 编号
     */
    void deleteAdjustmentItem(Long id);

    /**
     * 获得仓储管理-盘库调整单-明细
     *
     * @param id 编号
     * @return 仓储管理-盘库调整单-明细
     */
    AdjustmentItemRespVO getAdjustmentItem(Long id);

    /**
     * 获得仓储管理-盘库调整单-明细分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-盘库调整单-明细分页
     */
    PageResult<AdjustmentItemDO> getAdjustmentItemPage(AdjustmentItemPageReqVO pageReqVO);
}
