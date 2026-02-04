package com.syj.eplus.module.mms.api.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode MANUFACTURE_NOT_EXISTS = new ErrorCode(1_013_001_001, "加工单不存在");
    ErrorCode MANUFACTURE_SKU_STOCK_NOT_EXISTS = new ErrorCode(1_013_001_002, "加工单产品库存不存在");
    ErrorCode MANUFACTURE_SKU_STOCK_NOT_ENOUGH = new ErrorCode(1_013_001_003, "加工单产品库存不足");
    ErrorCode MANUFACTURE_SKU_NOT_EXISTS = new ErrorCode(1_013_002_001, "加工单产品不存在");

    ErrorCode MANUFACTURE_SKU_ITEM_NOT_EXISTS = new ErrorCode(1_013_003_001, "加工单子产品不存在");


}
