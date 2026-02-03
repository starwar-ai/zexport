package com.syj.eplus.module.fms.service.receipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptDetailReq;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptPageReqVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptRespVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.receipt.ReceiptDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 财务收款单 Service 接口
 *
 * @author ePlus
 */
public interface ReceiptService {

    /**
     * 创建财务收款单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReceipt(@Valid ReceiptSaveReqVO createReqVO);

    List<ReceiptDO> batchCreateReceipt(List<ReceiptSaveReqVO> createReqVOList);
    /**
     * 更新财务收款单
     *
     * @param updateReqVO 更新信息
     */
    void updateReceipt(@Valid ReceiptSaveReqVO updateReqVO);

    /**
     * 删除财务收款单
     *
     * @param id 编号
     */
    void deleteReceipt(Long id);

    /**
     * 获得财务收款单
     *
     * @param receiptDetailReq 明细请求实体
     * @return 财务收款单
     */
    ReceiptRespVO getReceipt(ReceiptDetailReq receiptDetailReq);

    /**
     * 获得财务收款单分页
     *
     * @param pageReqVO 分页查询
     * @return 财务收款单分页
     */
    PageResult<ReceiptRespVO> getReceiptPage(ReceiptPageReqVO pageReqVO);

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

    /**
     * 更新付款单审批信息
     *
     * @param id                付款单id
     * @param processInstanceId 流程实例id
     */
    void updateApprovalMessage(Long id, String processInstanceId);

    /**
     * 收款单确认
     *
     * @param receiptId 收单id
     */
    void confirmReceipt(Long receiptId);

    /**
     * 根据业务编号获取收款单数量
     * @param businessType 业务类型
     * @param businessCode 业务编号
     * @return 收款单数量
     */
    Long getOrderNumByBusinessCode(Integer businessType,String businessCode);

    /**
     * 批量删除认领创建的收款单
     * @param businessType 业务类型
     * @param businessCode 业务编号
     * @param businessSubjectType 业务科目类型
     * @param businessSubjectCode 业务科目编号
     */
    void batchDeletedReceipt(Integer businessType,String businessCode,Integer businessSubjectType,String businessSubjectCode);
}