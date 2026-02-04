package com.syj.eplus.module.oa.api;

import java.util.Map;

public interface DictSubjectApi {

//    /**
//     * 更新字典值
//     *
//     * @param systemDictDataId 字典值id
//     * @param systemDictDataLabel 字典值label
//     * @param systemDictDataValue 字典值value
//     *
//     */
//    void updateSystemDictData(Long systemDictDataId,String systemDictDataLabel,String systemDictDataValue);
//
//    /**
//     * 根据字典值id删除
//     *
//     * @param systemDictDataId   字典值id
//     *
//     */
//    boolean deleteBySystemDictDataId(Long systemDictDataId);
//
//    /**
//     * 更新字典类型
//     *
//     * @param systemDictTypeId   字典类型id
//     * @param systemDictTypeName 字典类型名称
//     */
//    void updateSystemDictType(Long systemDictTypeId,String systemDictTypeName);
//
//
//    /**
//     * 根据字典类型id删除
//     *
//     * @param systemDictTypeId    字典类型id
//     *
//     */
//    boolean deleteBySystemDictTypeId(Long systemDictTypeId);

    /**
     * 获取科目配置缓存
     * @return 科目配置缓存
     */
    Map<Long,String> getDictSubjectMap();

}
