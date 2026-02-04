package com.syj.eplus.module.infra.api.formchange;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.service.formchange.FormChangeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/19 21:14
 */
@Service
public class FormChangeApiImpl implements FormChangeApi {

    @Resource
    private FormChangeService formChangeService;

    @Override
    public FormChangeDTO getFormChangeByName(String name) {
        return formChangeService.getFormChangeDTOByName(name);
    }

    @Override
    public Map<String, FormChangeDTO> getFormChangeDTOList(List<String> tableNames) {
        if (CollUtil.isEmpty(tableNames)) {
            return Map.of();
        }
        List<FormChangeDTO> formChangeDTOList = formChangeService.getFormChangeDTOList(tableNames);
        if (CollUtil.isEmpty(formChangeDTOList)) {
            return Map.of();
        }
        return formChangeDTOList.stream().collect(Collectors.toMap(FormChangeDTO::getName, s -> s, (o1, o2) -> o1));
    }

    @Override
    public AtomicReference<Integer> dealShipmentTable(Set<String> changeFieldName, Map<String, FormChangeDTO> formChangeDTOList, ModelKeyHolder modelKeyHolder, String tableName, AtomicReference<Integer> submitFlag, boolean mainTableFlag) {
        return formChangeService.dealShipmentTable(changeFieldName, formChangeDTOList, modelKeyHolder, tableName, submitFlag, mainTableFlag);
    }
}
