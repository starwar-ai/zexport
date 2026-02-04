package com.syj.eplus.framework.common.util;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description：字符串分割工具类
 * @Author：du
 * @Date：2024/4/23 16:59
 */
public class SplitUtil {
    public static List<Integer> splitToLongList(String str) {
        if (StrUtil.isEmpty(str)) {
            return Collections.emptyList();
        }
        String[] split = str.split(CommonDict.COMMA);
        if (split.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(split).map(Integer::valueOf).collect(Collectors.toList());
    }

    public static List<String> splitToStringList(String regex, String value) {
        if (StrUtil.isEmpty(value)) {
            return Collections.emptyList();
        }
        String[] split = value.split(regex);
        if (split.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(split).distinct().toList();
    }
}
