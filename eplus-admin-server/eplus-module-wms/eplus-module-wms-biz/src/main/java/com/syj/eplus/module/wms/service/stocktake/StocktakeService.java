package com.syj.eplus.module.wms.service.stocktake;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeDetailReq;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 仓储管理-盘点单 Service 接口
 *
 * @author ePlus
 */
public interface StocktakeService {

    /**
     * 创建仓储管理-盘点单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStocktake(@Valid StocktakeSaveReqVO createReqVO);

    /**
     * 更新仓储管理-盘点单
     *
     * @param updateReqVO 更新信息
     */
    void updateStocktake(@Valid StocktakeSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-盘点单
     *
     * @param id 编号
     */
    void deleteStocktake(Long id);

    /**
     * 获得仓储管理-盘点单
     *
     * @param stocktakeDetailReq 明细请求实体
     * @return 仓储管理-盘点单
     */
    StocktakeRespVO getStocktake(StocktakeDetailReq stocktakeDetailReq);

    /**
     * 获得仓储管理-盘点单分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-盘点单分页
     */
    PageResult<StocktakeRespVO> getStocktakePage(StocktakePageReqVO pageReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);
    // ==================== 子表（仓储管理-盘点单-明细） ====================

    /**
     * 获得仓储管理-盘点单-明细分页
     *
     * @param pageReqVO   分页查询
     * @param stocktakeId 盘点单-主键
     * @return 仓储管理-盘点单-明细分页
     */
    PageResult<StocktakeItemDO> getStocktakeItemPage(PageParam pageReqVO, Long stocktakeId);

    /**
     * 创建仓储管理-盘点单-明细
     *
     * @param stocktakeItem 创建信息
     * @return 编号
     */
    Long createStocktakeItem(@Valid StocktakeItemDO stocktakeItem);

    /**
     * 更新仓储管理-盘点单-明细
     *
     * @param stocktakeItem 更新信息
     */
    void updateStocktakeItem(@Valid StocktakeItemDO stocktakeItem);

    /**
     * 删除仓储管理-盘点单-明细
     *
     * @param id 编号
     */
    void deleteStocktakeItem(Long id);

    /**
     * 获得仓储管理-盘点单-明细
     *
     * @param id 编号
     * @return 仓储管理-盘点单-明细
     */
    StocktakeItemDO getStocktakeItem(Long id);

    /**
     * 开始盘点
     * @param updateReqVO
     * @return
     */
    boolean counting(StocktakeSaveReqVO updateReqVO);

    /**
     * 盘点录入
     * @param updateReqVO
     * @return
     */
    boolean complete(StocktakeSaveReqVO updateReqVO);

    void exportExcel(Long id , String reportCode, HttpServletResponse response);
}
