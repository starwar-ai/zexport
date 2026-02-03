package com.syj.eplus.module.system.service.report;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.system.controller.admin.report.vo.*;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportRespVO;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportSaveReq;
import com.syj.eplus.module.system.dal.dataobject.report.ReportDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * 打印模板 Service 接口
 *
 * @author ePlus
 */
public interface ReportService {

    /**
     * 创建打印模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReport(@Valid ReportSaveReqVO createReqVO);

    /**
     * 更新打印模板
     *
     * @param updateReqVO 更新信息
     */
    void updateReport(@Valid ReportSaveReqVO updateReqVO);

    /**
     * 删除打印模板
     *
     * @param id 编号
     */
    void deleteReport(Long id);

    /**
     * 获得打印模板
     *
     * @param  req 编号和流程实例id
     * @return 打印模板
     */
    ReportInfoRespVO getReport(SystemReportDetailReq req);


    /**
     * 获取
     *
     * @param  companyId 账套id
     * @return 打印模板
     */
    List<ReportDO> getCompanySpecificReport(Long companyId,String reportCode);



    /**
     * 获得打印模板
     *
     * @param  code 编号
     * @return 打印模板
     */
    ReportDTO getReportByCode(String code);

    /**
     * 获得打印模板
     *
     * @param  code 编号
     * @return 打印模板
     */
    ReportDTO getReportByCodeAndCompanyId(String code,Long companyId);

    /**
     * 获得打印模板
     *
     * @param  code 编号
     * @return 打印模板
     */
    ReportDTO getSourceReport(String code, String sourceCode, Integer sourceType,Long companyId);

    /**
     * 获得打印模板
     *
     * @param  reportId 模板id
     * @return 打印模板
     */
    ReportDTO getReportById(Long reportId);

    /**
     * 获得打印模板分页
     *
     * @param pageReqVO 分页查询
     * @return 打印模板分页
     */
    PageResult<ReportDO> getReportPage(ReportPageReqVO pageReqVO);

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
     * @param custId 客户id
     * @param userId 用户id
     */
    void submitTask(Long custId, Long userId);

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
     * 打印
     *
     * @param params 参数
     * @return 打印输出路径
     */
    String print(String inputPath, String outputPath, HashMap<String, Object> params, String outputName);

    /**
     * 打印
     *
     * @param params 参数
     * @return 打印输出路径
     */
    void exportWord(HttpServletResponse response, String inputPath, String outputPath, HashMap<String, Object> params);



/*-----------------------------------------------------变更-----------------------------------------------------*/
    /**
     * 更新打印模板变更
     *
     * @param updateReqVO 更新信息
     */
    void changeReport(@Valid ChangeReportSaveReq updateReqVO);

    /**
     * 获得打印模板变更
     *
     * @param  req 编号和流程实例id
     * @return 打印模板
     */
    ChangeReportRespVO getChangeReport(Long id);

    /**
     * 获得打印模板变更分页
     *
     * @param pageReqVO 分页查询
     * @return 打印模板分页
     */
    PageResult<ChangeReportRespVO> getReportChangePage(ReportPageReqVO pageReqVO);

    /**
     * 变更通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveChangeTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 变更不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectChangeTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);

    /**
     * 变更提交任务
     *
     * @param custId 客户id
     * @param userId 用户id
     */
    void submitChangeTask(Long custId, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus);

    /**
     * 获取流程标识
     *
     * @return
     */
    String getChangeProcessDefinitionKey();

    void directlyUpdateReport(ReportChangeSaveReqVO updateReqVO);
}