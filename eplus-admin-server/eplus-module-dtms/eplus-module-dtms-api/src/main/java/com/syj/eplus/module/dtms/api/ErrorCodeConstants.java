package com.syj.eplus.module.dtms.api;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    // ========== 设计-任务单 TODO 补充编号 ==========
    ErrorCode DESIGN_NOT_EXISTS = new ErrorCode(1_011_001, "设计-任务单不存在");
    ErrorCode SPECIFIC_DESIGNER_NOT_EXISTS = new ErrorCode(1_011_001, "指定设计师不存在");
    ErrorCode DESIGNERID_NOT_EXISTS = new ErrorCode(1_011_001, "设计-任务单认领人为空");
    ErrorCode DESIGNERID_NOT_CONTAINS = new ErrorCode(1_011_001, "设计-任务单认领人不包含当前用户");
    // ========== 设计-认领明细 TODO 补充编号 ==========
    ErrorCode DESIGN_ITEM_NOT_EXISTS = new ErrorCode(1_011_002, "设计-认领明细不存在");
    ErrorCode DESIGN_ITEM_ALREADY_EXISTS = new ErrorCode(1_011_003, "已认领该任务，不可重复认领！");
    ErrorCode DESIGN_ITEM_EXPLAIN_EMPTY = new ErrorCode(1_011_004, "任务已超时，请输入异常说明！");

    // ========== 设计-工作总结 TODO 补充编号 ==========
    ErrorCode DESIGN_SUMMARY_NOT_EXISTS = new ErrorCode(1_011_004, "设计-工作总结不存在");
}
