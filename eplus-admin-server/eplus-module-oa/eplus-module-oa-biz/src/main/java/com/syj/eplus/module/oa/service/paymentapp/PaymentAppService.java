package com.syj.eplus.module.oa.service.paymentapp;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.*;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 公对公申请 Service 接口
 *
 * @author du
 */
public interface PaymentAppService {

    /**
     * 创建公对公申请
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createPaymentApp(@Valid PaymentAppSaveReqVO createReqVO) throws Exception;

    /**
     * 更新公对公申请
     *
     * @param updateReqVO 更新信息
     */
    void updatePaymentApp(@Valid PaymentAppSaveReqVO updateReqVO);

    /**
     * 删除公对公申请
     *
     * @param id 编号
     */
    void deletePaymentApp(Long id) throws Exception;

    /**
     * 获得公对公申请
     *
     * @param req 请求体
     * @return 公对公申请
     */
    PaymentAppRespVO getPaymentApp(PaymentAppDetailReq req);

    /**
     * 获得公对公申请分页
     *
     * @param pageReqVO 分页查询
     * @return 公对公申请分页
     */
    PageResult<PaymentAppRespVO> getPaymentAppPage(PaymentAppPageReqVO pageReqVO);

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
     * @param paymentAppId 出差报销id
     * @param userId       用户id
     */
    void submitTask(Long paymentAppId, Long userId);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 更新公对公申请单付款信息
     *
     * @param paymentAppDTO 公对公申请单DTO
     */
    void updatePaymentApp(PaymentAppDTO paymentAppDTO);

    /**
     * 批量更新公对公申请付款信息
     *
     * @param paymentAppDTOList 公对公申请单列表
     */
    void batchUpdatePaymentApp(List<PaymentAppDTO> paymentAppDTOList);

    PaymentAppDO getPaymentAppByCode(String code);


    LoanVO getNotPayLoanList(String venderCode);

    /**
     * 批量获取预付款单
     * @param codeList 付款单编号
     * @return 付款单精简列表
     */
    List<SimplePaymentAppResp> getPaymentAppList(List<String> codeList,boolean singleFlag);

    /**
     * 根据业务编号获取精简付款单
     * @param businessSubjectType 业务类型
     * @param businessSubjectCode 业务编号
     * @param codeList 编辑前编号列表
     * @return 精简付款单列表
     */
    List<SimplePaymentAppResp> getSimplePaymentAppList(Integer businessSubjectType,String businessSubjectCode,List<String> codeList,Long companyId,Integer relationType);

    String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId);

    JsonAmount getFeeShareAmountByCode(String businessCode);

    List<PaymentAppSendDetailRespVO>  exportPaymentAppSendDetailExcel(Long id);

    String printShipmentDetail(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId);

    /**
     * 验证关联编号是否存在
     *
     * @param relationCodes 关联编号列表
     * @return 是否存在
     */
    boolean validateRelationCodeExists(Collection<String> relationCodes);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);

    /**
     * 批量获取公对公申请单信息
     *
     * @param codes 公对公申请单编号列表
     * @return 公对公申请单信息
     */
    Map<String,PaymentAppDTO> getSimplePaymentMsgByCodes(Collection<String> codes);
}