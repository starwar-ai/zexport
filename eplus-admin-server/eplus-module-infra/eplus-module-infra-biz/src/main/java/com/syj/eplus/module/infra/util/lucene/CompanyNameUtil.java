package com.syj.eplus.module.infra.util.lucene;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公司名称处理工具类
 */
public class CompanyNameUtil {

    protected static final Logger log = LoggerFactory.getLogger(CompanyNameUtil.class);

    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String COUNTY = "county";

    public static final String PROVINCE_SUFFIX = "省";
    public static final String CITY_SUFFIX = "市";
    public static final String BRACKET_REGEX = "[（]|[）]|[(]|[)]";
    public static final String SUFFIX_REGEX = "[(集团|股份|有限|责任|分公司)]";
    public static final String REGEX = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)\" +\n" +
            "                \"?(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)\" +\n" +
            "                \"?(?<county>[^(区|市|县|旗|岛)]+区|.*?市|.*?县|.*?旗|.*?岛)\" +\n" +
            "                \"?(?<village>.*)";

    // 优化：预编译正则表达式，避免每次调用都重新编译
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX);
    private static final Pattern BRACKET_PATTERN = Pattern.compile(BRACKET_REGEX);
    private static final Pattern SUFFIX_PATTERN = Pattern.compile(SUFFIX_REGEX);

    public static String formatCompanyName(String targetCompanyName){
        if (StrUtil.isEmpty(targetCompanyName)) {
            return StrUtil.EMPTY;
        }
        // 处理圆括号 - 使用预编译的Pattern
        targetCompanyName = BRACKET_PATTERN.matcher(targetCompanyName).replaceAll(CommonDict.EMPTY_STR);
        // 处理公司相关关键词 - 使用预编译的Pattern
        targetCompanyName = SUFFIX_PATTERN.matcher(targetCompanyName).replaceAll(CommonDict.EMPTY_STR);

        // 使用预编译的Pattern
        Matcher matcher = REGEX_PATTERN.matcher(targetCompanyName);
        while(matcher.find()){
            String province = matcher.group(PROVINCE);
            if (StrUtil.isNotBlank(province) && targetCompanyName.contains(province)){
                targetCompanyName = targetCompanyName.replace(province, CommonDict.EMPTY_STR);
            }
            String city = matcher.group(CITY);
            if (StrUtil.isNotBlank(city) && targetCompanyName.contains(city)){
                targetCompanyName = targetCompanyName.replace(city,CommonDict.EMPTY_STR);
            }
            String county = matcher.group(COUNTY);
            if (StrUtil.isNotBlank(county) && targetCompanyName.contains(county)){
                targetCompanyName = targetCompanyName.replace(county,CommonDict.EMPTY_STR);
            }
        }
        List<String> areaNameList = AreaUtils.getAreaName();
        if (CollUtil.isNotEmpty(areaNameList)){
            // 创建一个新的列表来存储处理过的名称
            for (String areaName : areaNameList) {
                // 假设后缀是“省”或“市”，并且它们出现在名称的最后
                areaName = StrUtil.removeSuffix(areaName, PROVINCE_SUFFIX);
                areaName = StrUtil.removeSuffix(areaName, CITY_SUFFIX);
                if (targetCompanyName.contains(areaName)){
                    targetCompanyName = targetCompanyName.replace(areaName, CommonDict.EMPTY_STR);
                }
            }
        }
        return targetCompanyName;
    }

}

