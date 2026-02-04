package com.syj.eplus.module.exms.enums;


import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode EXHIBITION_NOT_EXISTS = new ErrorCode(1_010_014_001, "展会不存在");
    ErrorCode EXHIBITION_DATE_NOT_EXISTS = new ErrorCode(1_010_014_001, "展会完成时间异常");
    ErrorCode EVENT_CATEGORY_NOT_EXISTS = new ErrorCode(1_010_014_002, "展会系列不存在");
    ErrorCode EVENT_CATEGORY_IN_USED = new ErrorCode(1_010_014_002, "该展会系列正在使用中，不可删除");

}
