package com.syj.eplus.module.oa.service.reimbService;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbCloseReq;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface ReimbService {

    /**
     * 创建出差报销
     *
     * @param reimbDO 创建信息
     * @return 编号
     */
    Long createReimb(@Valid ReimbDO reimbDO);

    /**
     * 更新出差报销
     *
     * @param reimbDO 更新信息
     */
    int updateReimb(ReimbDO reimbDO);

    /**
     * 删除出差报销
     *
     * @param id 编号
     */
    int deleteReimb(Long id);

    /**
     * 获取报销单详情
     *
     * @param id
     * @return
     */
    ReimbRespVO getReimbResp(Long id);

    /**
     * 获取报销单分页
     *
     * @param pageReqVO 分页请求实体
     * @return 报销单分页列表
     */
    PageResult<ReimbRespVO> getReimbPage(ReimbPageReqVO pageReqVO,Integer freeShareSourceType);


    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId);

    /**
     * 更新打印次数
     *
     * @param id 单据id
     */
    void updatePrintTimes(Long id, Integer printStatus);

    /**
     * 根据报销单号批量获取报销单
     *
     * @param idList 报销单id列表
     * @return
     */
    Map<Long, ReimbRespVO> getBatchReimbResp(List<Long> idList);

    /**
     * 更新报销单中付款信息
     *
     * @param paymentAppDTO 付款信息DTO
     */
    void updatePaymentMessage(PaymentAppDTO paymentAppDTO);

    /**
     * 批量更新支付状态
     *
     * @param paymentAppDTOList 支付信息
     */
    void batchUpdatePaymentStatus(List<PaymentAppDTO> paymentAppDTOList);

    /**
     * 更新票夹子状态
     *
     * @param id 票夹子id
     */
    void updateInvoiceHolderStatus(Long id,Integer status);

    String print(Long id, String reportCode, String sourceCode, Integer sourceType, Long companyId, FeeShareSourceTypeEnum busType);

//    getReimbAuxiliaryPage();

    /**
     * 创建报销明细
     * @param reimbDetailDTO 报销明细
     */
    void createReimbDetail(ReimbDetailDTO reimbDetailDTO);

    /**
     * 获取报销明细
     * @return 报销明细
     */
    List<ReimbDetailDTO> getReimbDetail();

    JsonAmount getFeeShareAmountByCode(String businessCode);

    /**
     * 根据报销单id获取实际报销人
     * @param reimbId 报销单id
     * @return 实际报销人id
     */
    Long getActualUserByReimbId(Long reimbId);

    /**
     * 处理还款逻辑
     * @param reimbRespVO 报销详情
     */
    void dealRepayApp(ReimbRespVO reimbRespVO);

    void closeReimb(ReimbCloseReq reimbCloseReq);

    void submitTask(Long reimbId,String definitionKey);

    /**
     * 更新其他报销节点审批人
     * @param reimbId 报销id
     * @param userId 审批人
     */
    void updateApproveUserByInstanceId(Long reimbId,Long userId);

    /**
     * 还款
     * @param reimbId 报销id
     */
    void repayLoanApp(Long reimbId);
}
