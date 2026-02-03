package cn.iocoder.yudao.module.bpm.api.task;

import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRespDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 流程实例 Api 接口
 *
 * @author 芋道源码
 */
public interface BpmProcessInstanceApi {

    /**
     * 创建流程实例（提供给内部）
     *
     * @param userId      用户id
     * @param processKey  流程标识
     * @param auditableId 业务id
     * @return
     */
    String createProcessInstance(Long userId, String processKey, Long auditableId);

    String createProcessInstance(Long userId, String processKey, String businessKey, Long auditableId);

    /**
     * 创建流程实例（提供给内部）
     *
     * @param userId      用户id
     * @param processKey  流程标识
     * @param auditableId 业务id
     * @param variables   条件表达式
     * @param assignee    提前设置的审批人
     * @return
     */
    String createProcessInstance(Long userId, String processKey, Long auditableId, Map<String, Object> variables,Map<String,List<Long>> assignee);

    String getBpmProcessInstanceId(Long auditableId, String definitionKey);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO);

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqVO, String operatorExtsKey);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO);


    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqVO, String operatorExtsKey);

    BpmTaskRespDTO getBpmTask(String processInstanceId);

    /**
     * 根据流程实例名称获取拓展信息的业务id
     *
     * @param processInstanceId 流程实例id
     * @return 流程任务业务id
     */
    Long selectAuditAbleIdByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例id获取流程发起人id
     *
     * @param processInstanceId 流程实例id
     * @return 流程发起人id
     */
    Long getStartUserIdByProcessInstanceId(String processInstanceId);

    /**
     * 将流程任务分配给指定用户
     *
     * @param userId            用户编号
     * @param processInstanceId 任务编号
     * @param assigneeUserId    新审批人的用户编号
     */
    void transferTask(Long userId, String processInstanceId, Long assigneeUserId);

    /**
     * 根据流程实例id获取表单数据
     *
     * @param processInstanceId 流程实例id
     * @return 表单数据
     */
    Map<String, Object> getVariablesByProcessInstanceId(String processInstanceId);

    /**
     * 取消流程实例
     * @param userId 用户编号
     * @param processInstanceId 流程实例的编号
     */
    void cancelProcessInstance(Long userId,String processInstanceId);

    /**
     * 根据流程实例id获取各节点审批人
     */
    Map<String,String> getApproverByProcessInstance(String processInstanceId);
}
