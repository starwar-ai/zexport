package cn.iocoder.yudao.module.infra.api.config;

import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Service
public class ConfigApiImpl implements ConfigApi {

    @Resource
    private ConfigService configService;

    @Override
    public String getValueByConfigKey(String configKey) {
        ConfigDO configByKey = configService.getConfigByKey(configKey);
        if (Objects.isNull(configByKey)) {
            return null;
        }
        return configByKey.getValue();
    }

    @Override
    public Map<String, String> getConfigMap() {
        return configService.getConfigMap();
    }
}
