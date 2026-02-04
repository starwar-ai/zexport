package cn.iocoder.yudao.module.bpm.api.event;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * {@link BpmProcessInstanceResultEvent} 的监听器
 *
 * @author 芋道源码
 */
public abstract class BpmProcessInstanceResultEventListener
        implements ApplicationListener<BpmProcessInstanceResultEvent> {

    private static final Logger log = LoggerFactory.getLogger(BpmProcessInstanceResultEventListener.class);

    @Override
    public final void onApplicationEvent(BpmProcessInstanceResultEvent event) {
        if (StrUtil.isNotEmpty(getProcessDefinitionKey())&&StrUtil.equals(event.getProcessDefinitionKey(), getProcessDefinitionKey())) {
            onEvent(event);
            return;
        }
            String businessKey = event.getBusinessKey();
        String processDefinitionKeyByBusinessId = null;
        try {
            processDefinitionKeyByBusinessId = getProcessDefinitionKeyByBusinessId(Long.parseLong(businessKey));
        } catch (NumberFormatException ignore) {
            log.error("businessKey转换出错: businessKey-{}", businessKey);
            return;
        }
        if (StrUtil.isNotEmpty(processDefinitionKeyByBusinessId)&&StrUtil.equals(event.getProcessDefinitionKey(), processDefinitionKeyByBusinessId)){
                onEvent(event);
            }
    }

    /**
     * @return 返回监听的流程定义 Key
     */
    protected abstract String getProcessDefinitionKey();

    /**
     * 通过业务主键获取对应流程模型(仅适用变更)
     *
     * @return 返回监听的流程定义 Key
     */
    protected String getProcessDefinitionKeyByBusinessId(Long id) {
        return null;
    }

    /**
     * 处理事件
     *
     * @param event 事件
     */
    protected abstract void onEvent(BpmProcessInstanceResultEvent event);

}
