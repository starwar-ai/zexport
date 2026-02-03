package cn.iocoder.yudao.module.infra.api.config;

import java.util.Map;

public interface ConfigApi {

    String getValueByConfigKey(String configKey);

    /**
     * 获取配置项map
     *
     * @return 配置项map
     */
    Map<String, String> getConfigMap();
}
