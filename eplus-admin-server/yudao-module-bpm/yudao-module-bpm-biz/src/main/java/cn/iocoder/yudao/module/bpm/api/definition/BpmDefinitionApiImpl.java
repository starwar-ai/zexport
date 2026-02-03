package cn.iocoder.yudao.module.bpm.api.definition;

import cn.iocoder.yudao.module.bpm.api.definition.dto.BpmNodeUser;
import cn.iocoder.yudao.module.bpm.service.definition.BpmTaskAssignRuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/14/10:23
 * @Description:
 */
@Service
public class BpmDefinitionApiImpl implements BpmDefinitionApi{
    @Resource
    private BpmTaskAssignRuleService bpmTaskAssignRuleService;

    @Override
    public List<BpmNodeUser> getAllAssigneeByModuleId(String modelKey, String processInstanceId) {
        return bpmTaskAssignRuleService.getAllAssigneeByModuleId(modelKey, processInstanceId);
    }
}
