package com.syj.eplus.module.scm.service.concessionrelease;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseDetailReq;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleasePageReqVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseRespVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.concessionrelease.ConcessionReleaseDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 让步放行 Service 接口
 *
 * @author zhangcm
 */
public interface ConcessionReleaseService {

    /**
     * 创建让步放行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<CreatedResponse> createConcessionRelease(@Valid ConcessionReleaseSaveReqVO createReqVO);

    /**
     * 更新让步放行
     *
     * @param updateReqVO 更新信息
     */
    void updateConcessionRelease(@Valid ConcessionReleaseSaveReqVO updateReqVO);

    /**
     * 删除让步放行
     *
     * @param id 编号
     */
    void deleteConcessionRelease(Long id);

    /**
     * 获得让步放行
     *
     * @param  concessionReleaseDetailReq 明细请求实体
     * @return 让步放行
     */
    ConcessionReleaseRespVO getConcessionRelease( ConcessionReleaseDetailReq concessionReleaseDetailReq);

    /**
     * 获得让步放行分页
     *
     * @param pageReqVO 分页查询
     * @return 让步放行分页
     */
    PageResult<ConcessionReleaseRespVO> getConcessionReleasePage(ConcessionReleasePageReqVO pageReqVO);
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
    void submitTask(Long id, Long userId, Map<String, Object> variable);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     * @param processInstanceId 流程实例id
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId);

    ConcessionReleaseDO getConcessionReleaseById(Long id);

    /**
     * 通过流程实例id获取业务员
     * @param instanceId 流程实例id
     * @return 业务员
     */
    UserDept getAssigneeByInstanceId(String instanceId);

    /**
     * 通过流程实例id获取业务员对应部门负责人
     * @param instanceId 流程实例id
     * @return 业务员
     */
    Set<Long> getManagerAssigneeByInstanceId(String instanceId);

    /**
     * 通过流程实例id获取采购员对应部门负责人
     * @param qualityInspectionId 验货id
     * @return 业务员
     */
    Set<Long> getPurchaseManagerAssigneeByInstanceId(Long qualityInspectionId);
}