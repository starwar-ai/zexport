package cn.iocoder.yudao.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 模块加载监听器
 *
 * 在应用启动完成后,输出已加载的模块信息
 *
 * @author Claude Code
 */
@Slf4j
@Component
public class ModuleLoaderListener implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private ModuleProperties moduleProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<String> enabledModules = moduleProperties.getEnabledModules();

        log.info("========================================");
        log.info("模块化启动配置");
        log.info("========================================");
        log.info("已启用模块数量: {}", enabledModules.size());
        log.info("已启用模块列表: {}", String.join(", ", enabledModules));

        if (!moduleProperties.getDisabledModules().isEmpty()) {
            log.info("已禁用模块: {}", String.join(", ", moduleProperties.getDisabledModules()));
        }

        log.info("========================================");
        log.info("提示:");
        log.info("1. 可通过 application-modules.yaml 配置启用/禁用模块");
        log.info("2. 禁用不需要的模块可以提高启动速度和减少内存占用");
        log.info("3. 核心模块(system/infra/bpm)始终启用,不可禁用");
        log.info("========================================");
    }
}
