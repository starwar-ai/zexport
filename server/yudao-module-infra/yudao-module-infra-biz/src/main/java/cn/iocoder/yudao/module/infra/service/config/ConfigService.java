package cn.iocoder.yudao.module.infra.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.config.vo.*;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 参数配置 Service 接口
 *
 * @author 芋道源码
 */
public interface ConfigService {

    /**
     * 创建参数配置
     *
     * @param createReqVO 创建信息
     * @return 配置编号
     */
    Long createConfig(@Valid ConfigSaveReqVO createReqVO);

    /**
     * 更新参数配置
     *
     * @param updateReqVO 更新信息
     */
    void updateConfig(@Valid ConfigSaveReqVO updateReqVO);

    /**
     * 删除参数配置
     *
     * @param id 配置编号
     */
    void deleteConfig(Long id);

    /**
     * 获得参数配置
     *
     * @param id 配置编号
     * @return 参数配置
     */
    ConfigDO getConfig(Long id);

    /**
     * 根据参数键，获得参数配置
     *
     * @param key 配置键
     * @return 参数配置
     */
    ConfigDO getConfigByKey(String key);

    /**
     * 获得参数配置分页列表
     *
     * @param reqVO 分页条件
     * @return 分页列表
     */
    PageResult<ConfigDO> getConfigPage(@Valid ConfigPageReqVO reqVO);

    /**
     * 获取配置项map
     *
     * @return 配置项map
     */
    Map<String, String> getConfigMap();

    /**
     * 获取参数列表
     *
     * @return
     */
    List<ConfigRespVO> getConfigList();

    List<ConfigMapVO> getInvoiceRate(Integer isAbroad);

    List<ConfigJsonRespVO> getJsonList(String configType,String type);
}
