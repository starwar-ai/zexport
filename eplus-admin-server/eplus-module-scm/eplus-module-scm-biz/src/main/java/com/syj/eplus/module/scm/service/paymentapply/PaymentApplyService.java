package com.syj.eplus.module.scm.service.paymentapply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.scm.api.paymentapply.dto.PaymentApplyDTO;
import com.syj.eplus.module.scm.controller.admin.paymentapply.vo.*;
import com.syj.eplus.module.scm.dal.dataobject.paymentapply.PaymentApplyDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 付款申请主 Service 接口
 *
 * @author du
 */
public interface PaymentApplyService {

    /**
     * 创建付款申请主
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createPaymentApply(@Valid PaymentApplySaveReqVO createReqVO);

    /**
     * 更新付款申请主
     *
     * @param updateReqVO 更新信息
     */
    void updatePaymentApply(@Valid PaymentApplySaveReqVO updateReqVO);

    /**
     * 删除付款申请主
     *
     * @param id 编号
     */
    void deletePaymentApply(Long id);

    /**
     * 获得付款申请主
     *
     * @param paymentApplyDetailReq 明细请求实体
     * @return 付款申请主
     */
    PaymentApplyRespVO getPaymentApply(PaymentApplyDetailReq paymentApplyDetailReq);

    /**
     * 获得付款申请主分页
     *
     * @param pageReqVO 分页查询
     * @return 付款申请主分页
     */
    PageResult<PaymentApplyDO> getPaymentApplyPage(PaymentApplyPageReqVO pageReqVO);

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
     * 获取付款申请中间页信息
     *
     * @param idList 付款计划id列表
     * @return 付款申请中间页信息
     */
    PaymentApplyRespVO getPaymentApplyMessage(List<Long> idList,Integer step);


    /**
     * 回写付款信息
     *
     * @param paymentAppDTOList 付款信息
     */
    void batchWriteBackPaymentMsg(List<PaymentAppDTO> paymentAppDTOList);


    /**
     * 创建付款单
     *
     * @param paymnetApplyId 付款申请主键
     */
    void createPaymentOrder(Long paymnetApplyId);

    /**
     * 取消流程实例
     * @param userId 用户编号
     * @param processInstanceId 流程实例的编号
     */
    void cancelProcessInstance(Long userId,String processInstanceId);


    /**
     * 反审核
     * @param id 付款申请主键
     */
    void antiAudit(Long id);

    PaymentApplyDTO getPaymentApplyByCode(String code);

    /**
     * 作废
     * @param closePaymentApplyReq 付款申请单
     */
    void closePaymentApply(ClosePaymentApplyReq closePaymentApplyReq);

    String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId);

    List<PaymentApplyDO> getListByPurchaseCode(String contractCode);

    /**
     * 验证采购合同是否已存在付款申请
     * @param purchaseContractCode 采购合同编号
     * @return 是否已存在付款申请
     */
    boolean validateTransformPaymentApply(String purchaseContractCode);

    /**
     * 根据支付申请信息列表，更新采购付款计划的附件信息。
     * 该方法的主要流程如下：
     * 1. 检查传入的支付申请DTO列表是否为空，若为空则直接返回；
     * 2. 提取所有包含附件的支付申请，并按code分组收集其附件；
     * 3. 查询与这些支付申请相关的付款计划ID；
     * 4. 构建一个映射关系：将付款计划ID与其对应的附件列表进行关联；
     * 5. 合并相同付款计划ID下的附件，避免重复添加；
     * 6. 批量更新采购付款计划的附件字段。
     * @param paymentAppDTOList 支付申请信息列表，不能为空
     */
    void batchWriteAnnex(List<PaymentAppDTO> paymentAppDTOList);
}