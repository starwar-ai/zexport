package com.syj.eplus.module.scm.service.purchaseregistration;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;

import javax.validation.Valid;
import java.util.List;

/**
 * 采购跟单登记 Service 接口
 *
 * @author du
 */
public interface PurchaseRegistrationService {

    /**
     * 创建采购跟单登记
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseRegistration(@Valid PurchaseRegistrationSaveReqVO createReqVO);

    /**
     * 更新采购跟单登记
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseRegistration(@Valid PurchaseRegistrationSaveReqVO updateReqVO);

    /**
     * 删除采购跟单登记
     *
     * @param id 编号
     */
    void deletePurchaseRegistration(Long id);

    /**
     * 获得采购跟单登记
     *
     * @param purchaseRegistrationDetailReq 明细请求实体
     * @return 采购跟单登记
     */
    PurchaseRegistrationRespVO getPurchaseRegistration(PurchaseRegistrationDetailReq purchaseRegistrationDetailReq);

    /**
     * 获得采购跟单登记分页
     *
     * @param pageReqVO 分页查询
     * @return 采购跟单登记分页
     */
    PageResult<PurchaseRegistrationRespVO> getPurchaseRegistrationPage(PurchaseRegistrationPageReqVO pageReqVO);

    /**
     * 获得采购跟单登记明细分页
     *
     * @param pageReqVO 分页查询
     * @return 采购跟单登记分页
     */
    PageResult<PurchaseRegistrationItemResp> getPurchaseRegistrationItemPage(PurchaseRegistrationPageReqVO pageReqVO);

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
    void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId, Integer status);

    /**
     * 更改开票通知明细状态
     *
     * @param itemList 采购跟单明细
     */
    void updateInvoicingNoticesStatus(List<PurchaseRegistrationItem> itemList);

    /**
     * 更改采购合同明细
     *
     * @param itemList 采购跟单明细
     * @param currency 币种
     */
    void updatePurchaseContractItem(List<PurchaseRegistrationItem> itemList, String currency);

    /**
     * 批量复核跟单登记
     * @param reviewReq 请求体
     */
    void batchReviewPurchaseRegistration(ReviewRegistrationReq reviewReq);

    /**
     * 作废
     * @param id 请求体
     */
    void closePurchaseRegistration(Long id);

}