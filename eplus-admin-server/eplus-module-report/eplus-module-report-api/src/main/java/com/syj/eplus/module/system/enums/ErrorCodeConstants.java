package com.syj.eplus.module.system.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode REPORT_NOT_EXISTS = new ErrorCode(1_010_001_001, "打印模板不存在");
    ErrorCode INPUT_PAth_NOT_EXISTS = new ErrorCode(1_010_001_002, "模板文件不存在");
    ErrorCode REPORT_ERROR = new ErrorCode(1_010_001_003, "生成报表错误");
    ErrorCode REPORT_PARA_ERROR = new ErrorCode(1_010_001_004, "模板参数异常");
    ErrorCode REPORT_MUL = new ErrorCode(1_010_002_001, "打印模板重复");
    ErrorCode REPORT_FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_010_001_004, "未找到打印模板相关变更字段配置");
}