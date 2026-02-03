package com.syj.eplus.module.sms.service.quotation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 报价单主 Service 接口
 *
 * @author ePlus
 */
public interface QuotationService {

    /**
     * 创建报价单主
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuotation(@Valid QuotationSaveReqVO createReqVO);

    /**
     * 更新报价单
     *
     * @param updateReqVO 更新信息
     */
    void updateQuotation(@Valid QuotationSaveReqVO updateReqVO);

    /**
     * 删除报价单主
     *
     * @param id 编号
     */
    void deleteQuotation(Long id);

    /**
     * 获得报价单主
     *
     * @param  quotationDetailReq 明细请求实体
     * @return 报价单主
     */
    QuotationRespVO getQuotation( QuotationDetailReq quotationDetailReq);

    /**
     * 获得报价单主分页
     *
     * @param pageReqVO 分页查询
     * @return 报价单主分页
     */
    PageResult<QuotationRespVO> getQuotationPage(QuotationPageReqVO pageReqVO);
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
    void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    /**
     * 结案
     *
     * @param id 请求
     */
    void finish(Long id);

    /**
     * 导出
     *
     * @param id
     * @param reportCode 模板code
     * @return
     */
    void exportExcel(Long id, Long reportId ,String reportCode, String unit, HttpServletResponse response);

    /**
     * 获得报价单子分页
     *
     * @param pageReqVO 分页查询
     * @return 报价单主分页
     */
    PageResult<QuotationItemRespVO> getQuotationItemPage(QuotationItemPageReqVO pageReqVO);

    /**
     * 打印
     *
     * @param id
     * @param reportCode 模板code
     * @param reportId   打印模板id
     * @param companyId  归属公司主键
     * @param unit  公制/英制
     * @return
     */
    String print(Long id, String reportCode, Long reportId, Long companyId, Integer totalPurchaseFlag, String unit);


}