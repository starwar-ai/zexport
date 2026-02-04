package com.syj.eplus.module.fms.api.payment.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1_008_001_001 段
 */
public interface ErrorCodeConstants {

    // ========== 财务管理 1_008_001_001 ==========
    ErrorCode RECEIPT_NOT_EXISTS = new ErrorCode(1_008_001_001, "收款单不存在");
    ErrorCode UPDATE_PAYMENT_STATUS_FAIL = new ErrorCode(1_008_001_002, "付款状态更新失败");
    ErrorCode BANK_REGISTRATION_NOT_EXISTS = new ErrorCode(1_008_001_003, "银行登记单不存在");
    ErrorCode PAYMENT_CODE_EMPTY = new ErrorCode(1_008_001_004, "付款单编号为空");
    ErrorCode PAYMENT_NOT_EXIST = new ErrorCode(1_008_001_005, "付款单不存在");

}
