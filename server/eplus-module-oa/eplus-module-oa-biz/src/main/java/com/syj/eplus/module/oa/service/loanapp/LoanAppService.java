package com.syj.eplus.module.oa.service.loanapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.*;
import com.syj.eplus.module.oa.controller.admin.paymentapp.vo.LoanDescVO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 借款申请单 Service 接口
 *
 * @author ePlus
 */
public interface LoanAppService {

    String SN_TYPE = "SN_LOAN_APP";

    /**
     * 创建借款申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLoanApp(@Valid LoanAppSaveReqVO createReqVO);

    /**
     * 更新借款申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateLoanApp(@Valid LoanAppSaveReqVO updateReqVO);

    /**
     * 删除借款申请单
     *
     * @param id 编号
     */
    void deleteLoanApp(Long id);

    /**
     * 获得借款申请单
     *
     * @param req 请求实体
     * @return 借款申请单
     */
    LoanAppInfoRespVo getLoanApp(LoanAppDetailReq req);

    /**
     * 获得借款申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 借款申请单分页
     */
    PageResult<LoanAppRespVO> getLoanAppPage(LoanAppPageReqVO pageReqVO);

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
     * @param loanAppId 借款单id
     */
    void submitTask(Long loanAppId, Long userId);

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
     * 更新借款申请时间
     *
     * @param loanAppId 审核业务id
     */
    void updateLoanDateAndApplyerId(Long loanAppId);

    /**
     * 根据id列表获取借款单精简列表
     *
     * @param idList id列表
     * @return 借款单精简列表
     */
    List<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOList(List<Long> idList, Long travelReimbId, Long repayAppId);

    /**
     * 根据申请人获取借款单精简列表
     *
     * @param pageReqVO 过滤条件
     * @return 借款单精简分页列表
     */
    PageResult<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOListByApplyerId(LoanAppPageReqVO pageReqVO);

    /**
     * 获得借款申请单DTO
     *
     * @param req 请求实体
     * @return 借款申请单
     */
    SimpleLoanAppRespDTO getsimpleLoanAppDTO(LoanAppDetailReq req);

    /**
     * 根据借款单编号获取借款单id列表(模糊查询)
     *
     * @param code 借款单编号
     * @return 借款单id列表
     */
    List<Long> getIdListByCode(String code);

    /**
     * 更新借款单支付信息
     *
     * @param paymentAppDTO
     */
    void updatePaymentStatus(PaymentAppDTO paymentAppDTO);

    /**
     * 更新借款单中已还金额及剩余未还款金额
     *
     * @param repayAmount 本次还款金额
     * @param id          主键
     */
    void updateRepayAmount(JsonAmount repayAmount, Long id);

    /**
     * 批量更新借款单支付信息
     *
     * @param paymentAppDTOList 借款单支付信息列表
     */
    void batchUpdaPaymentStatus(List<PaymentAppDTO> paymentAppDTOList);

    LoanAppDO getLoanAppById(Long id);

    List<LoanDescVO> getNotDoneVenderLoanList(String code);


    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveVenderTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectVenderTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 提交任务
     *
     * @param loanAppId 借款单id
     */
    void submitVenderTask(Long loanAppId, Long userId);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getVenderProcessDefinitionKey();

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



}