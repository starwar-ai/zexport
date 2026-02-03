package com.syj.eplus.framework.common.util;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @Description：转换工具类
 * @Author：du
 * @Date：2024/5/24 11:23
 */
public class TransformUtil {
    /**
     * 用于转换操作记录中字段
     * @param changeRecords 操作记录精简列表
     * @param idList 转换业务唯一标识列表
     * @param queryService 查询的sevice类
     * @param fieldNamePredicate 字段比较函数
     * @param transformer 字段名称
     * @param <E> 服务类返回实体单元
     * @param <V> 转换后的字段值
     */
    public static  <E, V> void transformField(List<ChangeRecord> changeRecords,
                                       List<String> idList,
                                       Function<List<String>, Map<String, E>> queryService,
                                       Predicate<String> fieldNamePredicate,
                                       Function<E, V> transformer) {
        // 执行对应逻辑层查询方法
        Map<String, E> dataMap = queryService.apply(idList);
        if (CollUtil.isEmpty(dataMap)) {
            return;
        }

            if (CollUtil.isNotEmpty(changeRecords)) {
            changeRecords.forEach(record -> {
                String fieldName = record.getFieldName();
                // 判断是否当前传入字段
                if (fieldNamePredicate.test(fieldName)) {
                    // 获取当前字段值
                    Object value = record.getValue();
                    if (Objects.nonNull(value)) {
                        // 获取对应查询实体
                        E data = dataMap.get((String) value);
                        if (Objects.nonNull(data)) {
                            // 获取实体中需转换字段值
                            V transformedValue = transformer.apply(data);
                            // 重写操作记录中该字段值
                            record.setValue(String.valueOf(transformedValue));
                        }
                    }
                    Object oldValue = record.getOldValue();
                    if (Objects.nonNull(oldValue)) {
                        E data = dataMap.get((String) oldValue);
                        if (Objects.nonNull(data)) {
                            V transformedOldValue = transformer.apply(data);
                            record.setOldValue(String.valueOf(transformedOldValue));
                        }
                    }
                }
            });
        }
    }

    public static JsonCompanyPath getLastCompanyPath(JsonCompanyPath companyPath){
        if (Objects.isNull(companyPath)){
            return null;
        }
        return Objects.isNull(companyPath.getNext())? companyPath:getLastCompanyPath(companyPath.getNext());
    }

    public static Long getLastCompanyId(JsonCompanyPath companyPath){
        JsonCompanyPath lastCompanyPath = getLastCompanyPath(companyPath);
        return Objects.isNull(lastCompanyPath)?null:lastCompanyPath.getId();
    }

    /**
     * 将 JsonCompanyPath 链表转换为 List<JsonCompanyPath>
     * @param companyPath 链表头节点
     * @return 包含所有节点的 List
     */
    public static List<JsonCompanyPath> convertJsonCompanyPathToList(JsonCompanyPath companyPath) {
        List<JsonCompanyPath> result = new ArrayList<>();
        addNodeToList(companyPath, result);
        return result;
    }

    /**
     * 递归方法，将节点添加到 List 中
     * @param node 当前节点
     * @param result 结果 List
     */
    private static void addNodeToList(JsonCompanyPath node, List<JsonCompanyPath> result) {
        if (node == null) {
            return;
        }
        result.add(node);
        addNodeToList(node.getNext(), result);
    }
}
