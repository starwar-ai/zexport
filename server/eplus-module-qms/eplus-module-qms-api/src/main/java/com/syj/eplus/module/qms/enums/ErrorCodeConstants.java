package com.syj.eplus.module.qms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    // ========== 验货单 TODO 补充编号 ==========
    ErrorCode QUALITY_INSPECTION_NOT_EXISTS = new ErrorCode(1_007_001_001, "验货单不存在");

    ErrorCode QUALITY_INSPECTION_ITEM_NOT_EXISTS = new ErrorCode(1_007_002_001, "验货单-明细不存在");

    ErrorCode QUALITY_CODE_NOT_SAME = new ErrorCode(1_007_002_002, "验货单明细不属于同一个验货单");
    ErrorCode QUALITY_ITEM_SKU_SUCCESS = new ErrorCode(1_007_002_003, "验货单明细中存在已经验货成功的产品");


    /**
     * 操作日志标识
     */
     String OPERATOR_EXTS_KEY = "code";

}
