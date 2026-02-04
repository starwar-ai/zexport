package com.syj.eplus.module.infra.api.formchange;

import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public interface FormChangeApi {


    /**
     * 根据名称获取表更表信息以及字段配置
     *
     * @param name 变更表名
     * @return 变更表信息
     */
    FormChangeDTO getFormChangeByName(String name);

    /**
     * 获取表单对应字段配置
     *
     * @param tableNames 表单名称列表
     * @return 字段配置列表
     */
    Map<String, FormChangeDTO> getFormChangeDTOList(List<String> tableNames);

    /**
     * 处理变更表单字段
     *
     * @param changeFieldName   变更字段列表
     * @param formChangeDTOList 变更表单字段配置
     * @param modelKeyHolder    模型标识持有者
     * @param tableName         表名
     * @param submitFlag        提交标识
     * @param mainTableFlag     主表标识
     * @return 提交标识
     */
    AtomicReference<Integer> dealShipmentTable(Set<String> changeFieldName, Map<String, FormChangeDTO> formChangeDTOList, ModelKeyHolder modelKeyHolder, String tableName, AtomicReference<Integer> submitFlag, boolean mainTableFlag);
}
