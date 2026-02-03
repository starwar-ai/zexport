package com.syj.eplus.framework.common.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * P6Spy SQL监控日志格式化
 */
public class P6SpyLogger implements MessageFormattingStrategy {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }
        // 过滤掉定时/异步任务相关的SQL（如QRTZ_、infra_job、_JOB等，不区分大小写）
        String upperSql = sql.toUpperCase();
        if (upperSql.contains("QRTZ_")
                || upperSql.contains("INFRA_JOB")
                || upperSql.contains("INFRA_JOB_LOG")
                || upperSql.contains("_JOB")
                || upperSql.contains("_TIMER_JOB")
                || upperSql.contains("_EXTERNAL_JOB")) {
            return "";
        }
        // 格式化SQL
        String sqlFormat = sql.replaceAll("[\\s\\n ]+", " ");
        // 构建日志消息
        String message = "\n============== SQL执行日志 ==============\n" +
                "执行时间：" + format.format(new Date()) + "\n" +
                "执行SQL：" + sqlFormat + "\n" +
                "执行时间：" + elapsed + " ms" +
                "\n=======================================";
        return message;
    }
} 