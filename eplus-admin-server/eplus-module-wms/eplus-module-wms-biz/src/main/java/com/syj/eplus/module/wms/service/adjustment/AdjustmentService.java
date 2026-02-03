package com.syj.eplus.module.wms.service.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentDetailReq;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentDO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;

import javax.validation.Valid;

/**
 * 仓储管理-盘库调整单 Service 接口
 *
 * @author ePlus
 */
public interface AdjustmentService {

    /**
     * 创建仓储管理-盘库调整单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAdjustment(@Valid AdjustmentSaveReqVO createReqVO);

    /**
     * 更新仓储管理-盘库调整单
     *
     * @param updateReqVO 更新信息
     */
    void updateAdjustment(@Valid AdjustmentSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-盘库调整单
     *
     * @param id 编号
     */
    void deleteAdjustment(Long id);

    /**
     * 获得仓储管理-盘库调整单
     *
     * @param adjustmentDetailReq 明细请求实体
     * @return 仓储管理-盘库调整单
     */
    AdjustmentRespVO getAdjustment(AdjustmentDetailReq adjustmentDetailReq);

    /**
     * 获得仓储管理-盘库调整单分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-盘库调整单分页
     */
    PageResult<AdjustmentDO> getAdjustmentPage(AdjustmentPageReqVO pageReqVO);
    // ==================== 子表（仓储管理-盘库调整单-明细） ====================

    /**
     * 获得仓储管理-盘库调整单-明细分页
     *
     * @param pageReqVO    分页查询
     * @param adjustmentId 调整单主键
     * @return 仓储管理-盘库调整单-明细分页
     */
    PageResult<AdjustmentItemDO> getAdjustmentItemPage(PageParam pageReqVO, Long adjustmentId);

    /**
     * 创建仓储管理-盘库调整单-明细
     *
     * @param adjustmentItem 创建信息
     * @return 编号
     */
    Long createAdjustmentItem(@Valid AdjustmentItemDO adjustmentItem);

    /**
     * 更新仓储管理-盘库调整单-明细
     *
     * @param adjustmentItem 更新信息
     */
    void updateAdjustmentItem(@Valid AdjustmentItemDO adjustmentItem);

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
    AdjustmentItemDO getAdjustmentItem(Long id);

}
