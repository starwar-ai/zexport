package com.syj.eplus.module.oa.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.mysql.dictsubject.DictSubjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DictSubjectApiImpl implements DictSubjectApi {
    @Resource
    private DictSubjectMapper dictSubjectMapper;

    @Override
    public Map<Long, String> getDictSubjectMap() {
        List<DictSubjectDO> dictSubjectDOList = dictSubjectMapper.selectList();
        if (CollUtil.isEmpty(dictSubjectDOList)){
            return Map.of();
        }
        return dictSubjectDOList.stream().collect(Collectors.toMap(DictSubjectDO::getId, s-> Optional.ofNullable(s.getFeeName()).orElse(CommonDict.EMPTY_STR)));
    }

//    @Override
//    public void updateSystemDictData(Long systemDictDataId, String systemDictDataLabel, String systemDictDataValue) {
//        List<DictSubjectDO> dictSubjectList = dictSubjectMapper.selectList(DictSubjectDO::getSystemDictDataId, systemDictDataId);
//        if (CollUtil.isEmpty(dictSubjectList)) {
//            return;
//        }
//        dictSubjectList.forEach(item -> {
//            item.setSystemDictDataLabel(systemDictDataLabel);
//            item.setSystemDictDataValue(systemDictDataValue);
//        });
//        dictSubjectMapper.updateBatch(dictSubjectList);
//    }
//
//    @Override
//    public boolean deleteBySystemDictDataId(Long systemDictDataId) {
//        List<DictSubjectDO> dictSubjectList = dictSubjectMapper.selectList(DictSubjectDO::getSystemDictDataId, systemDictDataId);
//        if (CollUtil.isEmpty(dictSubjectList)) {
//            return true;
//        }
//        List<Long> idList = dictSubjectList.stream().map(DictSubjectDO::getId).toList();
//        dictSubjectMapper.deleteBatchIds(idList);
//        return true;
//    }

//    @Override
//    public void updateSystemDictType(Long systemDictTypeId, String systemDictTypeName) {
//        List<DictSubjectDO> dictSubjectList = dictSubjectMapper.selectList(DictSubjectDO::getSystemDictTypeId, systemDictTypeId);
//        dictSubjectList.forEach(item -> {
//            item.setSystemDictTypeName(systemDictTypeName);
//        });
//        dictSubjectMapper.updateBatch(dictSubjectList);
//    }
//
//    @Override
//    public boolean deleteBySystemDictTypeId(Long systemDictTypeId) {
//        List<DictSubjectDO> dictSubjectList = dictSubjectMapper.selectList(DictSubjectDO::getSystemDictTypeId, systemDictTypeId);
//        List<Long> idList = dictSubjectList.stream().map(DictSubjectDO::getId).toList();
//        dictSubjectMapper.deleteBatchIds(idList);
//        return true;
//    }
}
