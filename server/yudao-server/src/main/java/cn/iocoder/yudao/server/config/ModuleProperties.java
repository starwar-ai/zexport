package cn.iocoder.yudao.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块化配置属性
 *
 * 用于控制哪些业务模块在启动时被加载
 * 配置文件: application-modules.yaml
 *
 * @author Claude Code
 */
@Data
@Component
@ConfigurationProperties(prefix = "eplus.modules")
public class ModuleProperties {

    /**
     * 核心模块配置
     */
    private CoreConfig core = new CoreConfig();

    /**
     * 业务模块配置
     */
    private BusinessConfig business = new BusinessConfig();

    /**
     * 禁用的模块列表
     */
    private List<String> disabledModules = new ArrayList<>();

    /**
     * 核心模块配置
     */
    @Data
    public static class CoreConfig {
        /**
         * 是否启用核心模块(默认true,不可禁用)
         */
        private boolean enabled = true;

        /**
         * 核心模块列表
         */
        private List<String> modules = new ArrayList<>();
    }

    /**
     * 业务模块配置
     */
    @Data
    public static class BusinessConfig {
        /**
         * 是否启用业务模块
         */
        private boolean enabled = true;

        /**
         * 启用的业务模块列表
         */
        private List<String> modules = new ArrayList<>();
    }

    /**
     * 判断某个模块是否启用
     *
     * @param moduleName 模块名称
     * @return true-启用, false-禁用
     */
    public boolean isModuleEnabled(String moduleName) {
        // 如果在禁用列表中,直接返回false
        if (disabledModules.contains(moduleName)) {
            return false;
        }

        // 核心模块始终启用
        if (core.isEnabled() && core.getModules().contains(moduleName)) {
            return true;
        }

        // 业务模块根据配置判断
        return business.isEnabled() && business.getModules().contains(moduleName);
    }

    /**
     * 获取所有启用的模块
     *
     * @return 启用的模块列表
     */
    public List<String> getEnabledModules() {
        List<String> enabledModules = new ArrayList<>();

        // 添加核心模块
        if (core.isEnabled()) {
            enabledModules.addAll(core.getModules());
        }

        // 添加业务模块
        if (business.isEnabled()) {
            enabledModules.addAll(business.getModules());
        }

        // 移除禁用的模块
        enabledModules.removeAll(disabledModules);

        return enabledModules;
    }
}
