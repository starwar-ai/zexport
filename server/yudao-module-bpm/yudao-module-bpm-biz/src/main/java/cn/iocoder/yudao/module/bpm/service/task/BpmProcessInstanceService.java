package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.*;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceExtDO;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程实例 Service 接口
 *
 * @author 芋道源码
 */
public interface BpmProcessInstanceService {

    /**
     * 获得流程实例
     *
     * @param id 流程实例的编号
     * @return 流程实例
     */
    ProcessInstance getProcessInstance(String id);

    /**
     * 获得流程实例列表
     *
     * @param ids 流程实例的编号集合
     * @return 流程实例列表
     */
    List<ProcessInstance> getProcessInstances(Set<String> ids);

    /**
     * 获得流程实例 Map
     *
     * @param ids 流程实例的编号集合
     * @return 流程实例列表 Map
     */
    default Map<String, ProcessInstance> getProcessInstanceMap(Set<String> ids) {
        return CollectionUtils.convertMap(getProcessInstances(ids), ProcessInstance::getProcessInstanceId);
    }

    /**
     * 获得流程实例的分页
     *
     * @param userId    用户编号
     * @param pageReqVO 分页请求
     * @return 流程实例的分页
     */
    PageResult<BpmProcessInstancePageItemRespVO> getMyProcessInstancePage(Long userId,
                                                                          @Valid BpmProcessInstanceMyPageReqVO pageReqVO);

    /**
     * 创建流程实例（提供给前端）
     *
     * @param userId      用户编号
     * @param createReqVO 创建信息
     * @return 实例的编号
     */
    String createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqVO createReqVO);

    /**
     * 创建流程实例（提供给内部）
     *
     * @param userId       用户编号
     * @param createReqDTO 创建信息
     * @return 实例的编号
     */
    String createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO createReqDTO);

    /**
     * 获得流程实例 VO 信息
     *
     * @param id 流程实例的编号
     * @return 流程实例
     */
    BpmProcessInstanceRespVO getProcessInstanceVO(String id);

    /**
     * 取消流程实例
     *
     * @param userId      用户编号
     * @param cancelReqVO 取消信息
     */
    void cancelProcessInstance(Long userId, @Valid BpmProcessInstanceCancelReqVO cancelReqVO);

    /**
     * 获得历史的流程实例
     *
     * @param id 流程实例的编号
     * @return 历史的流程实例
     */
    HistoricProcessInstance getHistoricProcessInstance(String id);

    /**
     * 获得历史的流程实例列表
     *
     * @param ids 流程实例的编号集合
     * @return 历史的流程实例列表
     */
    List<HistoricProcessInstance> getHistoricProcessInstances(Set<String> ids);

    /**
     * 获得历史的流程实例 Map
     *
     * @param ids 流程实例的编号集合
     * @return 历史的流程实例列表 Map
     */
    default Map<String, HistoricProcessInstance> getHistoricProcessInstanceMap(Set<String> ids) {
        return CollectionUtils.convertMap(getHistoricProcessInstances(ids), HistoricProcessInstance::getId);
    }

    /**
     * 创建 ProcessInstance 拓展记录
     *
     * @param instance 流程任务
     */
    void createProcessInstanceExt(ProcessInstance instance);

    /**
     * 更新 ProcessInstance 拓展记录为取消
     *
     * @param event 流程取消事件
     */
    void updateProcessInstanceExtCancel(FlowableCancelledEvent event);

    /**
     * 更新 ProcessInstance 拓展记录为完成
     *
     * @param instance 流程任务
     */
    void updateProcessInstanceExtComplete(ProcessInstance instance);

    /**
     * 更新 ProcessInstance 拓展记录为不通过
     *
     * @param id     流程编号
     * @param reason 理由。例如说，审批不通过时，需要传递该值
     */
    void updateProcessInstanceExtReject(String id, String reason);

    // TODO @hai：改成 getProcessInstanceAssigneesByTaskDefinitionKey(String id, String taskDefinitionKey)

    /**
     * 获取流程实例中，取出指定流程任务提前指定的审批人
     *
     * @param processInstanceId 流程实例的编号
     * @param taskDefinitionKey 流程任务定义的 key
     * @return 审批人集合
     */
    List<Long> getAssigneeByProcessInstanceIdAndTaskDefinitionKey(String processInstanceId, String taskDefinitionKey);

    /**
     * 根据流程实例名称获取拓展信息的业务id
     *
     * @param processInstanceId 流程实例id
     * @return 流程任务业务id
     */
    Long selectAuditAbleIdByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例标识及流程任务业务id获取流程实例拓展信息
     *
     * @param auditAbleId
     * @param instanceDefinitionKey
     * @return
     */
    BpmProcessInstanceExtDO getProcessInstanceExtByAuditAbleId(Long auditAbleId, String instanceDefinitionKey);

    /**
     * 获得指令流程实例的流程任务列表，包括所有状态的
     *
     * @param processInstanceId 流程实例的编号
     * @return 流程任务列表
     */
    List<BpmTaskRespVO> getTaskListByProcessInstanceId(String processInstanceId);

    /**
     * 处理相同节点审批人任务
     *
     * @param processInstanceId 流程实例编号
     * @param assigneeId        当前节点审批人
     */
    void dealSimpleTask(String taskId, String processInstanceId, Long assigneeId);

    /**
     * 根据流程实例id获取已通过审批人编号列表
     * @param processInstanceId 流程实例id
     * @return 已通过审批人列表
     */
    List<Long> getApproveAssigneeUserIdList(String processInstanceId);

    /**
     * 自动通过流程任务
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程模型id
     * @param assigneeUserId 审批人
     */
    void autoComplete(String taskId,String processInstanceId,String processDefinitionId,Long assigneeUserId);

    /**
     * 获取自动通过流程实例缓存
     * @return 自动通过流程实例缓存
     */
    Set<String> getAutoApproveInstanceCache();

    /**
     * 更新自动通过流程实例缓存
     * @param instanceId 流程实例id
     */
    void setAutoApproveInstanceCache(String instanceId);

    /**
     * 移除对应流程实例缓存
     * @param instanceId 流程实例id
     */
    void removeAutoApproveInstanceById(String instanceId);

    /**
     * 校验流程实例是否自动通过
     * @param instanceId 流程实例id
     * @return 是否自动通过
     */
    boolean validateInstanceAutoApproveFlag(String instanceId);
}
