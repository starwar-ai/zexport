package com.syj.eplus.module.scm.service.invoicingnotices;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 开票通知 Service 接口
 *
 * @author du
 */
public interface InvoicingNoticesService {

    /**
     * 创建开票通知
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createInvoicingNotices(@Valid InvoicingNoticesSaveReqVO createReqVO);

    /**
     * 更新开票通知
     *
     * @param updateReqVO 更新信息
     */
    void updateInvoicingNotices(@Valid InvoicingNoticesSaveReqVO updateReqVO);

    /**
     * 删除开票通知
     *
     * @param id 编号
     */
    void deleteInvoicingNotices(Long id);

    /**
     * 获得开票通知
     *
     * @param invoicingNoticesDetailReq 明细请求实体
     * @return 开票通知
     */
    InvoicingNoticesRespVO getInvoicingNotices(InvoicingNoticesDetailReq invoicingNoticesDetailReq);

    /**
     * 获得开票通知分页
     *
     * @param pageReqVO 分页查询
     * @return 开票通知分页
     */
    PageResult<InvoicingNoticesRespVO> getInvoicingNoticesPage(InvoicingNoticesPageReqVO pageReqVO);

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
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId);

    /**
     * 获取开票通知明细
     *
     * @param pageReqVO 请求体
     * @return 开票通知明细
     */
    PageResult<InvoicingNoticesItemResp> getInvoicingNoticesItemList(InvoicingNoticesPageReqVO pageReqVO);

    String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId,boolean mergeFlag);

    /**
     *  根据发票号获取供应商列表
     *
     * @param shipInvoiceCode 出运发票号
     * @return 开票通知
     */
    List<SimpleVenderRespDTO> getVenderListByShipInvoiceCode(String shipInvoiceCode );

    /**
     * 作废开票通知
     * @param id 开票通知id
     */
    void closeInvoicingNotices(Long id);

    /**
     * 根据采购项id获取开票数量
     * @param purchaseItemIds 采购项id
     * @return 开票数量
     */
    Map<Long,Integer> getInvoiceQuantityByPurchaseItemIds(List<Long> purchaseItemIds);
}