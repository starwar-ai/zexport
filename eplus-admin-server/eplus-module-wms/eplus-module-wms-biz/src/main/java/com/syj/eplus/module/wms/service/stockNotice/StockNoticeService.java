package com.syj.eplus.module.wms.service.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 仓储管理-入(出)库通知单 Service 接口
 *
 * @author ePlus
 */
public interface StockNoticeService extends IService<StockNoticeDO> {

    /**
     * 创建仓储管理-入(出)库通知单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    CreatedResponse createNotice(@Valid StockNoticeSaveReqVO createReqVO);

    /**
     * 更新仓储管理-入(出)库通知单
     *
     * @param updateReqVO 更新信息
     */
    void updateNotice(@Valid StockNoticeSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-入(出)库通知单
     *
     * @param id 编号
     */
    void deleteNotice(Long id);

    /**
     * 获得仓储管理-入(出)库通知单
     *
     * @param id 编号
     * @return 仓储管理-入(出)库通知单
     */
    StockNoticeRespVO getNotice(Long id,String processInstanceId);

    /**
     * 获得仓储管理-入(出)库通知单分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-入(出)库通知单分页
     */
    PageResult<StockNoticeRespVO> getNoticePage(StockNoticePageReqVO pageReqVO);
    // ==================== 子表（仓储管理-入(出)库通知单-明细） ====================

    /**
     * 获得仓储管理-入(出)库通知单-明细列表
     *
     * @param noticeId 入/出库通知单主键
     * @return 仓储管理-入(出)库通知单-明细列表
     */
    List<StockNoticeItemDO> getNoticeItemListByNoticeId(Long noticeId);

    /**
     * 入(出)库通知单转入(出)库单
     * @param id
     * @param shippingAddress
     * @return
     */
    List<CreatedResponse> toBill(Long id,Integer shippingAddress);

    /**
     * 通过采购合同主键获取入库单数量
     * @param id 采购合同主键
     * @return 入库单数量
     */
    Long getBillNumByPurchaseCode(Long id);

    /**
     * 获得仓储管理-拉柜通知单分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-入(出)库通知单分页
     */
    PageResult<StockNoticeRespVO> getTransportationNoticePage(StockNoticePageReqVO pageReqVO);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void containerTransportationExportExcel(Long id, String reportCode, HttpServletResponse response);

    /**
     * 作废
     * @param id 通知单id
     */
    void closeStockNotice(Long id);

    /**
     * 打印
     *
     * @param id
     * @param reportCode 模板code
     * @param reportId   打印模板id
     * @param companyId  归属公司主键
     * @return
     */
    String print(Long id, String reportCode, Long reportId, Long companyId);

    Map<String,Integer> getStockNoticeMapByPurchaseContractCode(String purchaseContractCode);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getInProcessDefinitionKey();

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getOutProcessDefinitionKey();

    /**
     * 更新审核状态
     *
     * @param auditableId 业务id
     * @param auditStatus 审核状态
     * @param processInstanceId 流程实例id
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    /**
     * 作废出入库通知单
     * @param id 业务id
     */
    void closeNotice(Long id);

    boolean validateManualStockNotice(String code);

    /**
     * 更新转换状态
     * @param code 业务编号
     */
    void updateConvertStatusByCode(String code);
}
