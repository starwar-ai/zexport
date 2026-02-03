package com.syj.eplus.module.infra.util.code;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.CODE_GENERATOR_ERROR;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/30 14:56
 */

public class CodeGeneratorUtil {

    /**
     * 年份跟月份取位规则
     */
    private static final String YEAR_MONTH_RULE = "%02d";
    /**
     * 自增编号取位规则
     */
    private static final String CODE_RULE = "%04d";

    protected static final Logger logger = LoggerFactory.getLogger(CodeGeneratorUtil.class);

    public static String getCodePrefixTheMonth(String beginFlag) {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        String yearFlag = String.format(YEAR_MONTH_RULE, year).substring(2, 4);
        int monthValue = date.getMonthValue();
        String monthFlag = String.format(YEAR_MONTH_RULE, monthValue);
        return beginFlag + yearFlag + monthFlag;
    }

    public static String selfIncrementCode(String code) {
        long incrementCode;
        try {
            incrementCode = Long.parseLong(code.substring(5, 9)) + 1;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            logger.error("[客户编号]自增编号转换失败code-{}", code, e);
            // 优化：抛出异常而不是返回null，避免NullPointerException
            throw exception(CODE_GENERATOR_ERROR);
        }
        return String.format(CODE_RULE, incrementCode);
    }
}
