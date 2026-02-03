package cn.iocoder.yudao.module.bpm.api.definition;

import cn.iocoder.yudao.module.bpm.api.definition.dto.BpmNodeUser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/14/10:23
 * @Description:
 */
public interface BpmDefinitionApi {

    /**
     * 根据模型标识跟流程实例id获取所有节点审批人信息
     * @param modelKey 模型标识
     * @param processInstanceId 流程实例id
     * @return 所有节点审批人信息
     */
    List<BpmNodeUser> getAllAssigneeByModuleId(String modelKey, String processInstanceId);
}
