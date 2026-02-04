package com.syj.eplus.framework.common.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义Logger，彻底杜绝空行输出
 * 使用SLF4J日志框架替代System.out,便于日志管理和级别控制
 */
public class NoEmptyLineLogger extends FormattedLogger {

    private static final Logger log = LoggerFactory.getLogger(NoEmptyLineLogger.class);

    @Override
    public void logText(String text) {
        if (text == null || "null".equals(text.trim()) || "".equals(text.trim())) {
            return;
        }
        // 使用SLF4J日志框架,支持日志级别控制
        // DEBUG级别,避免生产环境日志污染
        if (log.isDebugEnabled()) {
            log.debug("\u001b[0;32m{}\u001b[0m", text);
        }
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        return log.isDebugEnabled();
    }

    @Override
    public void logException(Exception e) {
        // 使用日志框架记录异常
        log.error("P6Spy Exception", e);
    }
} 