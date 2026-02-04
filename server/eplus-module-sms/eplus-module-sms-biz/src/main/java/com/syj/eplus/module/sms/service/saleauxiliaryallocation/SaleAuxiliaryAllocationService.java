package com.syj.eplus.module.sms.service.saleauxiliaryallocation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 销售合同辅料分摊 Service 接口
 *
 * @author zhangcm
 */
public interface SaleAuxiliaryAllocationService {

    /**
     * 创建销售合同辅料分摊
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSaleAuxiliaryAllocation(@Valid SaleAuxiliaryAllocationSaveReqVO createReqVO);

    /**
     * 更新销售合同辅料分摊
     *
     * @param updateReqVO 更新信息
     */
    void updateSaleAuxiliaryAllocation(@Valid SaleAuxiliaryAllocationSaveReqVO updateReqVO);

    /**
     * 删除销售合同辅料分摊
     *
     * @param id 编号
     */
    void deleteSaleAuxiliaryAllocation(Long id);

    /**
     * 获得销售合同辅料分摊
     *
* @param  saleAuxiliaryAllocationDetailReq 明细请求实体
     * @return 销售合同辅料分摊
     */
SaleAuxiliaryAllocationRespVO getSaleAuxiliaryAllocation( SaleAuxiliaryAllocationDetailReq saleAuxiliaryAllocationDetailReq);

    /**
     * 获得销售合同辅料分摊分页
     *
     * @param pageReqVO 分页查询
     * @return 销售合同辅料分摊分页
     */
    PageResult<SaleAuxiliaryAllocationDO> getSaleAuxiliaryAllocationPage(SaleAuxiliaryAllocationPageReqVO pageReqVO);
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

    List<SaleAuxiliaryAllocationDO> getListBySaleCode(String code);

    void allocationSaleAuxiliaryAllocation(AllocationReqVO reqVO);

    void cancelAllocationSaleAuxiliaryAllocation(Long itemId);

    List<SaleAuxiliaryAllocationRespVO> getAllocation(Long saleId, Long purchaseItemId);
}