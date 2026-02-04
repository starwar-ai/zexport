package com.syj.eplus.module.scm.service.purchaseauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 采购合同辅料分摊 Service 接口
 *
 * @author zhangcm
 */
public interface PurchaseAuxiliaryAllocationService {

    /**
     * 创建采购合同辅料分摊
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseAuxiliaryAllocation(@Valid PurchaseAuxiliaryAllocationSaveReqVO createReqVO);

    /**
     * 更新采购合同辅料分摊
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseAuxiliaryAllocation(@Valid PurchaseAuxiliaryAllocationSaveReqVO updateReqVO);

    /**
     * 删除采购合同辅料分摊
     *
     * @param id 编号
     */
    void deletePurchaseAuxiliaryAllocation(Long id);

    /**
     * 获得采购合同辅料分摊
     *
* @param  purchaseAuxiliaryAllocationDetailReq 明细请求实体
     * @return 采购合同辅料分摊
     */
PurchaseAuxiliaryAllocationRespVO getPurchaseAuxiliaryAllocation( PurchaseAuxiliaryAllocationDetailReq purchaseAuxiliaryAllocationDetailReq);

    /**
     * 获得采购合同辅料分摊分页
     *
     * @param pageReqVO 分页查询
     * @return 采购合同辅料分摊分页
     */
    PageResult<PurchaseAuxiliaryAllocationDO> getPurchaseAuxiliaryAllocationPage(PurchaseAuxiliaryAllocationPageReqVO pageReqVO);
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
    * @param id 业务id
    * @param userId    用户id
    */
    void submitTask(Long id, Long userId);

    /**
    * 更新审核状态状态
    *
    * @param auditableId 审核业务id
    * @param auditStatus 审核状态
    */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    List<PurchaseAuxiliaryAllocationDO> getListByPurcahseItemIdList(List<Long> idList);

    void batchInsert(List<PurchaseAuxiliaryAllocationDO> allocationDoList,Long contractId);

    List<PurchaseAuxiliaryAllocationDO> getListByPurchaseContractCode(String code);

    void updatePurchaseAuxiliaryAllocationAllocation(PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO);

    List<PurchaseAuxiliaryAllocationDO> getListByPurchaseItemId(Long contractId);

    void deletePurchaseAuxiliaryAllocationByItemId(Long itemId);
}