package cn.iocoder.yudao.module.infra.service.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.controller.admin.config.vo.*;
import cn.iocoder.yudao.module.infra.convert.config.ConfigConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.dal.mysql.config.ConfigMapper;
import cn.iocoder.yudao.module.infra.enums.config.ConfigTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.*;

/**
 * 参数配置 Service 实现类
 */
@Service
@Slf4j
@Validated
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConfig(ConfigSaveReqVO createReqVO) {
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(null, createReqVO.getKey());

        // 插入参数配置
        ConfigDO config = ConfigConvert.INSTANCE.convert(createReqVO);
        config.setType(ConfigTypeEnum.CUSTOM.getType());
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigSaveReqVO updateReqVO) {
        // 校验自己存在
        validateConfigExists(updateReqVO.getId());
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(updateReqVO.getId(), updateReqVO.getKey());

        // 更新参数配置
        ConfigDO updateObj = ConfigConvert.INSTANCE.convert(updateReqVO);
        configMapper.updateById(updateObj);
    }

    @Override
    public void deleteConfig(Long id) {
        // 校验配置存在
        ConfigDO config = validateConfigExists(id);
        // 内置配置，不允许删除
        if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
            throw exception(CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
        }
        // 删除
        configMapper.deleteById(id);
    }

    @Override
    public ConfigDO getConfig(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public ConfigDO getConfigByKey(String key) {
        return configMapper.selectByKey(key);
    }

    @Override
    public PageResult<ConfigDO> getConfigPage(ConfigPageReqVO pageReqVO) {
        return configMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<String, String> getConfigMap() {
        List<ConfigDO> configDOList = configMapper.selectList();
        if (CollUtil.isEmpty(configDOList)) {
            return null;
        }
        return configDOList.stream().collect(Collectors.toMap(ConfigDO::getConfigKey, ConfigDO::getValue));
    }

    @Override
    public List<ConfigRespVO> getConfigList() {
        List<ConfigDO> configDOList = configMapper.selectList();
        if (CollUtil.isEmpty(configDOList)) {
            return null;
        }
        return BeanUtils.toBean(configDOList, ConfigRespVO.class);
    }

    @Override
    public List<ConfigMapVO> getInvoiceRate(Integer isAboard) {
        String type = "invoice.internet.rate";
        if(isAboard == 0){
            type = "invoice.internal.rate";
        }
        return Optional.ofNullable(configMapper.selectByKey(type))
                .map(ConfigDO::getValue).stream().flatMap(value -> Arrays.stream(value.split(",")))
                .map(s -> s.split(":"))
                .filter(parts -> parts.length == 2)
                .map(parts -> {
                    String key = parts[0];
                    String value = "0".equals(parts[1]) ? "" : parts[1];
                    return new ConfigMapVO().setKey(key).setValue(value);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigJsonRespVO> getJsonList(String configType,String type) {
        ConfigDO configDO;
        Optional<ConfigDO> first = configMapper.selectList(ConfigDO::getConfigKey, configType).stream().findFirst();
        if(first.isEmpty()){
          return null;
        }
        configDO = first.get();
        List<ConfigJsonRespVO> list =  JsonUtils.parseObject(configDO.getValue(), new TypeReference<>() {});
        if(CollUtil.isEmpty(list))
            return null;
        if(StrUtil.isEmpty(type)){
            return list;
        }
        return list.stream().filter(s-> Objects.equals(s.getType(), type)).toList();
    }

    @VisibleForTesting
    public ConfigDO validateConfigExists(Long id) {
        if (id == null) {
            return null;
        }
        ConfigDO config = configMapper.selectById(id);
        if (config == null) {
            throw exception(CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @VisibleForTesting
    public void validateConfigKeyUnique(Long id, String key) {
        ConfigDO config = configMapper.selectByKey(key);
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的参数配置
        if (id == null) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
        if (!config.getId().equals(id)) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
    }

}
