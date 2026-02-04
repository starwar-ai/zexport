package com.syj.eplus.module.home;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1_015_001_001 段
 */
public interface ErrorCodeConstants {
    ErrorCode HOME_TAB_NOT_EXISTS = new ErrorCode(1_015_001_004, "未查询到首页信息");
    ErrorCode INVOICE_HOLDER_NOT_EXISTS = new ErrorCode(1_015_001_005, "发票夹不存在");

}
