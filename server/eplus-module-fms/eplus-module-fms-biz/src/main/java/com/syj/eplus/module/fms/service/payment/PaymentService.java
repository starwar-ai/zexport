package com.syj.eplus.module.fms.service.payment;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentDTO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 财务付款表 Service 接口
 *
 * @author ePlus
 */
public interface PaymentService {

    /**
     * 创建财务付款表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPayment(@Valid PaymentSaveReqVO createReqVO);


    /**
     * 批量创建财务付款表
     *
     * @param createReqVOList 创建信息
     * @return 编号
     */
    void batchCreatePayment(List<PaymentSaveReqVO> createReqVOList);

    /**
     * 更新财务付款表
     *
     * @param updateReqVO 更新信息
     */
    void updatePayment(@Valid PaymentSaveReqVO updateReqVO);

    /**
     * 删除财务付款表
     *
     * @param id 编号
     */
    void deletePayment(Long id);

    /**
     * 获得财务付款表
     *
     * @param req 请求实体
     * @return 财务付款表
     */
    PaymentRespVO getPayment(PaymentDetailReq req);

    /**
     * 获得财务付款表分页
     *
     * @param pageReqVO 分页查询
     * @return 财务付款表分页
     */
    PageResult<PaymentRespVO> getPaymentPage(PaymentPageReqVO pageReqVO);

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
     * @param reqDTO 不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交任务
     *
     * @param paymentId 付款id
     * @param userId    用户id
     */
    void submitTask(Long paymentId, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 付款单确认
     *
     * @param reqVO 请求参数
     */
    void confirmPayment(PaymentConfirmReqVO reqVO) throws Exception;

    /**
     * 更新付款单审批信息
     *
     * @param id                付款单id
     * @param processInstanceId 流程实例id
     */
    void updateApprovalMessage(Long id, String processInstanceId);

    /**
     * 计划付款接口
     *
     * @param reqVO 请求参数
     */
    void planPayment(PlanPaymentReqVO reqVO);

    void planPaymentCancel(Long id);

    /**
     * 批量付款
     *
     * @param reqVO 请求参数
     */
    void batchConfirmPayment(BatchPaymentConfirmReqVO reqVO);

    /**
     * 更新费用归属状态
     * @param type 业务类型
     * @param code 业务编号
     * @param status 状态
     */
    void updateFeeShareStatus(Integer type, String code,Integer status);

    /**
     * 通过采购合同编号获取付款单数量
     * @param code 采购合同编号
     * @return 付款单数量
     */
    Long getPaymentNumByPurchaseContractCode(Integer businessType,String code);

    /**
     * 校验付款单
     * @param businessType 业务类型
     * @param businessCode 业务编号
     */
    void validatePayment(Integer businessType,String businessCode);

    /**
     * 付款单作废
     * @param id 付款单id
     */
    void closePayment(Long id);

    Map<String,PaymentDTO> getListByCodes(List<String> applyCodes);

    void batchPlanPayment(List<PlanPaymentReqVO> reqVO);

    void batchPayment(List<BatchPaymentReqVO> reqVO);

    /**
     * 作废付款单
     * @param closePaymentDTO dto
     */
    void closePayment(ClosePaymentDTO closePaymentDTO);
}