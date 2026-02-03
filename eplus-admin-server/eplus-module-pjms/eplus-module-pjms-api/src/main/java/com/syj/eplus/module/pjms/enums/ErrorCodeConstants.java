package com.syj.eplus.module.pjms.enums;


import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode PROJECT_NOT_EXISTS = new ErrorCode(1_010_015_001, "项目不存在");
    ErrorCode PROJECT_DATE_NOT_EXISTS = new ErrorCode(1_010_015_002, "项目日期不存在");

}
