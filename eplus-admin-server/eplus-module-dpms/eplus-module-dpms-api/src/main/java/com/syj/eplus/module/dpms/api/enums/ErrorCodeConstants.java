package com.syj.eplus.module.dpms.api.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1_016_001_001 段
 */
public interface ErrorCodeConstants {
    ErrorCode REPORT_NOT_EXISTS = new ErrorCode(1_015_001_001, "报表{}不存在");
    ErrorCode REPORT_ROLE_NOT_EXISTS = new ErrorCode(1_015_001_002, "报表权限不存在");
    ErrorCode REPORT_FUTURE_EXCEPTION = new ErrorCode(1_015_001_003, "报表任务执行异常或超时");
}
