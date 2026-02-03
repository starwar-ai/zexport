package com.syj.eplus.module.ems.enums;


import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode SEND_NOT_EXISTS = new ErrorCode(1_010_001_001, "寄件不存在");
    ErrorCode SEND_ID_NOT_EXISTS = new ErrorCode(1_010_001_002, "寄件ID不存在");

    ErrorCode FEESHARE_CUST_NULL = new ErrorCode(1_010_001_003, "费用归集客户为空");
    ErrorCode FEESHARE_VENDER_NULL = new ErrorCode(1_010_001_004, "费用归集供应商为空");
    ErrorCode SEND_CODE_MORE = new ErrorCode(1_010_001_005, "寄件编号存在多个");
    ErrorCode SEND_CODE_NOT_EXISTS = new ErrorCode(1_010_001_006, "寄件编号不存在");
    ErrorCode SEND_PRODUCT_NOT_EXISTS = new ErrorCode(1_010_002_001, "寄件产品不存在");

    ErrorCode SEND_BILL_NOT_EXISTS = new ErrorCode(1_010_003_001, "寄件导入单据不存在");


    ErrorCode FEE_SHARE_NOT_EXISTS = new ErrorCode(1_010_005_001, "寄件费用归属不存在");

    ErrorCode FEE_SHARE_ITEM_NOT_EXISTS = new ErrorCode(1_010_005_001, "寄件费用归属不存在");
    ErrorCode SEND_DEPT_IS_EMPTY = new ErrorCode(1_010_005_002, "寄件部门为空");

}
