package cn.iocoder.yudao.framework.operatelog.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.syj.eplus.framework.common.entity.DiffRecord;
import com.syj.eplus.framework.common.util.CompareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 10:46
 */
public class ChangeCompareUtil<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Field> getFields(Class<?> aClass) {
        List<Field> fields = new ArrayList<>();
        Field[] targetFields = aClass.getDeclaredFields();
        if (targetFields.length>0){
            fields.addAll(Arrays.asList(targetFields));
        }
        Class<?> superclass = aClass.getSuperclass();
        // 当父类非Object类则取出所有字段
        if (!Objects.isNull(superclass) && !Objects.equals(superclass, Object.class)){
            fields.addAll(getFields(superclass));
        }
        return fields;
    }

    public Set<String> transformObject(T oldVersion, T newVersion) {
        if (Objects.isNull(newVersion)) {
            return Set.of();
        }
        Set<String> changeFieldList = new HashSet<>();
        Map<String, Object> oldFieldMap = getFieldValueMap(oldVersion);
        if (CollUtil.isEmpty(oldFieldMap)){
            return changeFieldList;
        }
        Map<String, Object> newFieldMap = getFieldValueMap(newVersion);
        if (CollUtil.isEmpty(newFieldMap)){
            return changeFieldList;
        }
        oldFieldMap.forEach((fieldName,oldValue)->{
            Object newValue = newFieldMap.get(fieldName);
            boolean equalsFlag = CompareUtil.compare(oldValue, newValue);
            if (!equalsFlag){
                changeFieldList.add(fieldName);
            }
        });
        
        return changeFieldList;
    }

    private Map<String, Object> getFieldValueMap(Object t) {
        if (Objects.isNull(t)) {
            return Collections.emptyMap();
        }
        Class<?> clazz = t.getClass();
        Map<String, Object> map = new LinkedHashMap<>();
        while (clazz != null && !clazz.equals(Object.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
//                CompareField compareAnnotation = field.getAnnotation(CompareField.class);
//                if (Objects.isNull(compareAnnotation) || compareAnnotation.ignore()) {
//                    continue;
//                }
                ChangeIgnore changeIgnoreAnnotation = field.getAnnotation(ChangeIgnore.class);
                if (Objects.nonNull(changeIgnoreAnnotation) && changeIgnoreAnnotation.ignore()) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    map.put(fieldName, field.get(t));
                } catch (IllegalAccessException e) {
                    logger.error("[反射]获取{}字段值出错", fieldName, e);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }

    public Tuple transformChildList(List<DiffRecord<T>> diffRecords, Class<T> tarClazz) {
        List<T> result = new ArrayList<>();
        Set<String> changeFieldSetResult = new HashSet<>();
        if (CollUtil.isNotEmpty(diffRecords)) {
            diffRecords.forEach(diffRecord -> {
                T target;
                try {
                    target = tarClazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    logger.error("错误实例化类{},msg-{}", tarClazz.getName(), e.getMessage());
                    return;
                }
                // 处理未变更
                T oldItem = diffRecord.getOldItem();
                T newItem = diffRecord.getNewItem();
                target = newItem;
                if (ChangeTypeEnum.DELETED.getType().equals(diffRecord.getChangeFlag())){
                    target = BeanUtil.copyProperties(oldItem, tarClazz);
                }else {
                    target = BeanUtil.copyProperties(newItem, tarClazz);
                }
                Set<String> changeFieldSet = new HashSet<>();
                if (ChangeTypeEnum.UPDATE.getType().equals(diffRecord.getChangeFlag())) {
                    changeFieldSet= new ChangeCompareUtil<T>().transformObject(oldItem, newItem);
                }else if (ChangeTypeEnum.ADDED.getType().equals(diffRecord.getChangeFlag())||ChangeTypeEnum.DELETED.getType().equals(diffRecord.getChangeFlag())){
                    Field[] declaredFields = tarClazz.getDeclaredFields();
                    for (Field field : declaredFields) {
                        changeFieldSet.add(field.getName());
                    }
                }

                if (CollUtil.isNotEmpty(changeFieldSet)) {
                    changeFieldSetResult.addAll(changeFieldSet);
                    ((ChangeExInterface) target).setChangeFlag(diffRecord.getChangeFlag());
                }else {
                    if (Objects.nonNull(oldItem)&& Objects.nonNull(newItem)){
                        ((ChangeExInterface) target).setChangeFlag(ChangeTypeEnum.NOT_CHANGE.getType());
                    }else if (Objects.isNull(oldItem)){
                        ((ChangeExInterface) target).setChangeFlag(ChangeTypeEnum.ADDED.getType());
                    }else {
                        ((ChangeExInterface) target).setChangeFlag(ChangeTypeEnum.DELETED.getType());
                    }

                }
                target = castToEIfNecessary(target, tarClazz);
                T purchaseContractItemInfoRespVO = Optional.ofNullable(oldItem).orElse(newItem);
                if (purchaseContractItemInfoRespVO != null) {
                    try {
                        Method getIdMethod = purchaseContractItemInfoRespVO.getClass().getMethod("getId");
                        Object idValue = getIdMethod.invoke(purchaseContractItemInfoRespVO);
                        ((ChangeExInterface) target).setId((Long) idValue);
                    } catch (Exception e) {
                        logger.error("id赋值出错msg-{}", e.getMessage());
                    }
                }
                result.add(target);
            });
        }
        List<T> resultList = result.stream().filter(Objects::nonNull).toList();
        return new Tuple(resultList, changeFieldSetResult);
    }

    // 辅助方法进行类型转换，这在泛型擦除的场景下可能需要
    private <T> T castToEIfNecessary(Object obj, Class<T> clazz) {
        return clazz.cast(obj);
    }
}

