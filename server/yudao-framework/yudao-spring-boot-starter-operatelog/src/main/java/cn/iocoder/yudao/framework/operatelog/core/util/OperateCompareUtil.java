package cn.iocoder.yudao.framework.operatelog.core.util;


import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description：模块中详细记录变更比较工具类
 * @Author：du
 * @Date：2024/5/7 18:04
 */
public class OperateCompareUtil<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String FORMAT_TABEL = "【修改%s】：%s -> %s";
    private static final String FORMAT_TABEL_NULL = "【%s】";
    private static final String FORMAT_TABEL_OLD_NULL = "【新增%s】：%s";
    /**
     * 属性比较
     *
     * @param source 源数据对象
     * @param target 目标数据对象
     * @return 对应属性值的比较变化
     */
    public List<ChangeRecord> compare(T source, T target) {
        return compare(source, target, null);
    }


    /**
     * 属性比较
     *
     * @param source              源数据对象
     * @param target              目标数据对象
     * @param ignoreCompareFields 忽略比较的字段
     * @return 对应属性值的比较变化
     */
    public List<ChangeRecord> compare(T source, T target, List<String> ignoreCompareFields) {
        if (Objects.isNull(source) && Objects.isNull(target)) {
            return Collections.emptyList();
        }
        Map<String, CompareEntity> sourceMap = this.getFiledValueMap(source);
        Map<String, CompareEntity> targetMap = this.getFiledValueMap(target);
        if (sourceMap.isEmpty() && targetMap.isEmpty()) {
            return Collections.emptyList();
        }
        // 如果源数据为空，则只显示目标数据，不显示属性变化情况
        if (sourceMap.isEmpty()) {
            return doEmpty(targetMap, ignoreCompareFields);
        }
        // 如果源数据为空，则显示属性变化情况
        return doCompare(sourceMap, targetMap, ignoreCompareFields);
    }

    public String apply(String content, String fieldName, String sv, String tv, Integer operatorType) {
        if (StrUtil.isEmpty(content)) {
            return formatContent(fieldName, sv, tv, operatorType);
        }
        return content + "<br />" + formatContent(fieldName, sv, tv, operatorType);
    }

    private String formatContent(String fieldName, String sv, String tv, Integer operatorType) {
        sv = Objects.isNull(sv) ? CommonDict.EMPTY_STR : sv;
        tv = Objects.isNull(tv) ? CommonDict.EMPTY_STR : tv;
        if (StrUtil.isEmpty(sv) && StrUtil.isEmpty(tv)) {
            return String.format(FORMAT_TABEL_NULL, fieldName);
        } else if (StrUtil.isEmpty(sv) && StrUtil.isNotEmpty(tv)) {
            return String.format(FORMAT_TABEL_OLD_NULL, fieldName, tv);
        }
        return String.format(FORMAT_TABEL, fieldName, sv, tv);
    }

    private List<ChangeRecord> doEmpty(Map<String, CompareEntity> targetMap, List<String> ignoreCompareFields) {
        List<ChangeRecord> changeResult = new ArrayList<>();
        Collection<CompareEntity> values = targetMap.values();
        for (CompareEntity node : values) {
            Object o = Optional.ofNullable(node.getFieldValue()).orElse("");
            if (Objects.nonNull(ignoreCompareFields) && ignoreCompareFields.contains(node.getFieldKey())) {
                continue;
            }
            if (o.toString().length() > 0) {
                changeResult.add(new ChangeRecord().setValue(o.toString()).setFieldName(node.getFieldName()).setPictureFlag(node.getPictureFlag()));
            }
        }
        return changeResult;
    }

    private List<ChangeRecord> doCompare(Map<String, CompareEntity> sourceMap, Map<String, CompareEntity> targetMap, List<String> ignoreCompareFields) {
        Set<String> keys = sourceMap.keySet();
        List<ChangeRecord> changeResult = new ArrayList<>();
            for (String key : keys) {
            CompareEntity sn = sourceMap.get(key);
            CompareEntity tn = targetMap.get(key);
            if (Objects.nonNull(ignoreCompareFields) && ignoreCompareFields.contains(sn.getFieldKey())) {
                continue;
            }
            Object snFieldValue = Objects.isNull(sn.getFieldValue())? CommonDict.EMPTY_STR : sn.getFieldValue();
            Object tnFieldValue = Objects.isNull(tn.getFieldValue())? CommonDict.EMPTY_STR : tn.getFieldValue();
            String sv = snFieldValue.toString();
            String tv = tnFieldValue.toString();
            boolean isEqual;
            try {
                if (snFieldValue instanceof JsonAmount amount){
                    if (Objects.isNull(amount.getCheckAmount())||amount.getCheckAmount().compareTo(BigDecimal.ZERO) == 0) {
                        sv = CommonDict.EMPTY_STR;
                    }else {
                        sv = amount.getCheckAmount().stripTrailingZeros().toPlainString() + CommonDict.SPACE + amount.getCurrency();
                    }
                }else if (snFieldValue instanceof JsonWeight weight){
                    if (Objects.isNull(weight.getWeight())||weight.getWeight().compareTo(BigDecimal.ZERO) == 0) {
                        sv = CommonDict.EMPTY_STR;
                    }else {
                        sv = weight.getWeight().stripTrailingZeros().toPlainString() + CommonDict.SPACE + weight.getUnit();
                    }
                } else if (snFieldValue instanceof BigDecimal) {
                    sv = ((BigDecimal) snFieldValue).stripTrailingZeros().toPlainString();
                } else if (snFieldValue instanceof UserDept userDept){
                    if (Objects.isNull(userDept.getUserId())){
                        sv = CommonDict.EMPTY_STR;
                    }else {
                        sv = userDept.getNickname();
                    }
                } else{
                    sv = snFieldValue.toString();
                }
                if (tnFieldValue instanceof JsonAmount amount){
                    if (Objects.isNull(amount.getCheckAmount())||amount.getCheckAmount().compareTo(BigDecimal.ZERO) == 0) {
                        tv = CommonDict.EMPTY_STR;
                    }else {
                        tv = amount.getCheckAmount().stripTrailingZeros().toPlainString() + CommonDict.SPACE + amount.getCurrency();
                    }
                }else if (tnFieldValue instanceof JsonWeight weight){
                    if (Objects.isNull(weight.getWeight())||weight.getWeight().compareTo(BigDecimal.ZERO) == 0) {
                        tv = CommonDict.EMPTY_STR;
                    }else {
                        tv = weight.getWeight().stripTrailingZeros().toPlainString() + CommonDict.SPACE + weight.getUnit();
                    }

                } else if (tnFieldValue instanceof BigDecimal) {
                    tv = ((BigDecimal) tnFieldValue).stripTrailingZeros().toPlainString();
                } else if (tnFieldValue instanceof UserDept userDept){
                    if (Objects.isNull(userDept.getUserId())){
                        tv = CommonDict.EMPTY_STR;
                    }else {
                        tv = userDept.getNickname();
                    }
                } else{
                    tv = tnFieldValue.toString();
                }
                isEqual = sv.equals(tv);
            } catch (Exception e) {
                isEqual = snFieldValue.equals(tnFieldValue);
            }
            // 只有两者属性值不一致时, 才显示变化情况
            if (!isEqual) {
                ChangeRecord changeRecord = new ChangeRecord().setPictureFlag(sn.getPictureFlag()).setFieldName(sn.getFieldName());
                String enumType = sn.getEnumType();
                // 当枚举类型不为空时进行转换
                if (StrUtil.isNotEmpty(enumType)) {
                    String svDictDataLabel = CommonDict.EMPTY_STR;
                    String tvDictDataLabel = CommonDict.EMPTY_STR;
                    if (Objects.nonNull(snFieldValue) && Objects.nonNull(tnFieldValue)) {
                        String snValue = Objects.isNull(sn.getFieldValue())? CommonDict.EMPTY_STR : sn.getFieldValue().toString();
                        String tnValue = Objects.isNull(tn.getFieldValue())? CommonDict.EMPTY_STR : tn.getFieldValue().toString();
                        svDictDataLabel = DictFrameworkUtils.getDictDataLabel(enumType, snValue);
                        tvDictDataLabel = DictFrameworkUtils.getDictDataLabel(enumType, tnValue);
                    }
                    changeRecord.setOldValue(svDictDataLabel).setValue(tvDictDataLabel);
                } else {
                    changeRecord.setOldValue(sv).setValue(tv);
                }
                changeResult.add(changeRecord);
            }
        }
        return changeResult;
    }

    private Map<String, CompareEntity> getFiledValueMap(T t) {
        if (Objects.isNull(t)) {
            return Collections.emptyMap();
        }
        Field[] fields = t.getClass().getDeclaredFields();
        if (Objects.isNull(fields) || fields.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, CompareEntity> map = new LinkedHashMap();
        for (Field field : fields) {
            CompareField compareAnnotation = field.getAnnotation(CompareField.class);
            if (Objects.isNull(compareAnnotation)) {
                continue;
            }
            field.setAccessible(true);
            try {
                String fieldKey = field.getName();
                CompareEntity node = new CompareEntity();
                node.setFieldKey(fieldKey);
                node.setFieldValue(field.get(t));
                node.setFieldName(compareAnnotation.value());
                node.setPictureFlag(compareAnnotation.pictureFlag());
                node.setEnumType(compareAnnotation.enumType());
                map.put(field.getName(), node);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error("[实体对比]字段解析异常");
            }
        }
        return map;
    }
}
