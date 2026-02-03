package cn.iocoder.yudao.framework.operatelog.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.entity.DiffRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 16:10
 */
public class DiffUtil {

    protected static final Logger logger = LoggerFactory.getLogger(DiffUtil.class);


    // 比较两个列表并返回变更实体
    public static <T extends ChangeExInterface> List<DiffRecord<T>> compareLists(List<T> oldList, List<T> newList) {

        List<DiffRecord<T>> diffRecordList = new ArrayList<>();
        if (CollUtil.isEmpty(oldList) && CollUtil.isEmpty(newList)){
            return List.of();
        }
        // 旧集合为空 则新增
        if (CollUtil.isEmpty(oldList)){
           newList.forEach(s->{
               s.setChangeFlag(ChangeTypeEnum.ADDED.getType());
               diffRecordList.add(new DiffRecord<>(s,null,null,ChangeTypeEnum.ADDED.getType()));
           });
        }
        // 新集合为空 则删除
        if (CollUtil.isEmpty(newList)){
            oldList.forEach(s->{
                s.setChangeFlag(ChangeTypeEnum.DELETED.getType());
                diffRecordList.add(new DiffRecord<>(null,null,s,ChangeTypeEnum.DELETED.getType()));
            });
        }
        if (CollUtil.isNotEmpty(oldList) && CollUtil.isNotEmpty(newList)){
            Map<Long, T> newMap = newList.stream().filter(s->s.getId() != null).collect(Collectors.toMap(T::getId, s -> s, (s1, s2) -> s1));
            Map<Long, T> oldMap = oldList.stream().filter(s->s.getId() != null).collect(Collectors.toMap(T::getId, s -> s, (s1, s2) -> s1));
            oldList.forEach(s->{
              // 删除
              if ( newList.stream().noneMatch(item -> s.getId().equals(item.getId()))){
                  diffRecordList.add(new DiffRecord<>(null,null, s, ChangeTypeEnum.DELETED.getType()));
              }else {

                  diffRecordList.add(new DiffRecord<>(newMap.get(s.getId()),null, s, ChangeTypeEnum.UPDATE.getType()));
              }
          });
          newList.forEach(s->{
              if (Objects.isNull(s.getId())||!oldMap.containsKey(s.getId())){
                  diffRecordList.add(new DiffRecord<>(s,null,null, ChangeTypeEnum.ADDED.getType()));
              }
          });

        }

        return diffRecordList;
    }

    /**
     * 比较两个子项不同并返回结果
     *
     * @param oldList    更改前的子项列表
     * @param newList    更改后的子项列表
     * @param changeName 子项名称
     * @param fieldName  唯一标识(非id仅做操作日志展示)
     * @param <T>        子项实体类型
     * @return 变更结果
     */
    public static <T extends ChangeExInterface> List<ChangeRecord> compareListsToChangeRecord(List<T> oldList, List<T> newList, String changeName, String fieldName) {
        // 传入的集合为空则不比较 直接返回null
        if (CollUtil.isEmpty(oldList) && CollUtil.isEmpty(newList)) {
            return null;
        }
        Map<Object, T> oldMap = new HashMap<>();
        List<ChangeRecord> changeRecordList = new ArrayList<>();
        if (CollUtil.isNotEmpty(oldList)) {
            for (T oldItem : oldList) {
                // 删除
                if (CollUtil.isEmpty(newList) || !newList.stream().anyMatch(item -> oldItem.getId().equals(item.getId()))) {
                    String value = getValue(oldItem, fieldName);
                    changeRecordList.add(new ChangeRecord().setFieldName(String.format("删除%s-%s:", changeName, value)));
                }
                oldMap.put(oldItem.getId(), oldItem);
            }
        }
        if (CollUtil.isNotEmpty(newList)) {
            for (T newItem : newList) {
                //新增
                if (Objects.isNull(newItem.getId()) || !oldMap.containsKey(newItem.getId())) {
                    String value = getValue(newItem, fieldName);
                    changeRecordList.add(new ChangeRecord().setFieldName(String.format("新增%s-%s:", changeName, value)));
                    continue;
                }
                T oldItem = oldMap.get(newItem.getId());
                // 修改
                if (oldItem != null && !oldItem.equals(newItem)) {
                    String value = getValue(newItem, fieldName);
                    List<ChangeRecord> changeRecords = new OperateCompareUtil<T>().compare(oldItem, newItem);
                    if (CollUtil.isNotEmpty(changeRecords)) {
                        changeRecords.forEach(s -> {
                            s.setFieldName(String.format("修改%s-%s【%s】", changeName, value, s.getFieldName()));
                        });
                        changeRecordList.addAll(changeRecords);
                    }
                }
            }
        }
        return changeRecordList;
    }

    private static <T> String getValue(T t, String fieldName) {
        Class<?> aClass = t.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String name = declaredField.getName();
            if (fieldName.equals(name)) {
                declaredField.setAccessible(true);
                Object value = null;
                try {
                    value = declaredField.get(t);
                } catch (IllegalAccessException e) {
                    logger.error("[子项变更]获取字段值出错 fieldName-{}", name);
                }
                return Objects.isNull(value) ? null : value.toString();
            }
        }
        return null;
    }

}
