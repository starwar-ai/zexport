package com.syj.eplus.module.oa.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode LOAN_APP_NOT_EXISTS = new ErrorCode(1_004_002_001, "借款申请单不存在");
    ErrorCode TRAVEL_APP_NOT_EXISTS = new ErrorCode(1_004_001_001, "出差申请不存在");

    ErrorCode UNDERREVIEW_EDITTING_NOT_ALLOWED = new ErrorCode(1_001_001_008, "审核中,禁止修改");
    ErrorCode REIMB_NOT_EXISTS = new ErrorCode(1_004_003_001, "报销单不存在");
    ErrorCode TRAVEL_REIMB_DETAIL_NOT_EXISTS = new ErrorCode(1_004_003_002, "出差报销单明细不存在");
    ErrorCode REPAY_APP_NOT_EXISTS = new ErrorCode(1_004_003_003, "还款申请单不存在");
    ErrorCode PAYMENT_APP_NOT_EXISTS = new ErrorCode(1_004_003_004, "公对公申请单不存在或没权限");
    ErrorCode REIMB_REPAY_DETAIL_NOT_EXISTS = new ErrorCode(1_004_003_005, "报销还款详情不存在");

    ErrorCode TRAVEL_REIMVB_UPDATE_ERROR = new ErrorCode(1_004_003_006, "出差报销单修改失败");
    ErrorCode TRAVEL_REIMVB_DELETE_ERROR = new ErrorCode(1_004_003_007, "出差报销单删除失败");

    ErrorCode TRAVEL_REIMB_NOT_EXISTS = new ErrorCode(1_004_003_008, "出差报销单不存在");
    ErrorCode TRAVEL_AMOUNT_NOT_NULL = new ErrorCode(1_004_003_009, "报销金额不可为空");
    ErrorCode TRAVEL_AMOUNT_EXCEPTION = new ErrorCode(1_004_003_009, "报销金额计算异常");
    ErrorCode REIMBER_NULL = new ErrorCode(1_004_003_010, "报销人为空");
    ErrorCode REIMBER_SHARE_NOT_NULL = new ErrorCode(1_004_003_011, "费用归属不存在");
    ErrorCode GENERAL_REIMVB_UPDATE_ERROR = new ErrorCode(1_004_003_012, "一般费用报销单修改失败");
    ErrorCode GENERAL_REIMVB_DELETE_ERROR = new ErrorCode(1_004_003_013, "一般费用报销单删除失败");
    ErrorCode ENTERTAIN_REIMVB_DELETE_ERROR = new ErrorCode(1_004_003_014, "招待费报销单不存在");
    ErrorCode CUST_INFO_NOT_FIND = new ErrorCode(1_004_003_015, "未找到对应客户名称");
    ErrorCode REPAY_USER_NOT_NULL = new ErrorCode(1_004_003_016, "还款人不能为空");
    ErrorCode SUBJECT_NOT_EXISTS = new ErrorCode(1_004_003_017, "科目不存在");
    ErrorCode DICT_SUBJECT_NOT_EXISTS = new ErrorCode(1_004_003_018, "类别配置不存在");
    ErrorCode DICT_SUBJECT_MUL = new ErrorCode(1_004_003_019, "类别配置重复");
    ErrorCode GENERAL_REIMVB_FEESHARE_CUST_NULL = new ErrorCode(1_004_003_017, "费用归集类型为客户时，客户列表不能为空");
    ErrorCode GENERAL_REIMVB_FEESHARE_VENDER_NULL = new ErrorCode(1_004_003_017, "费用归集类型为供应商时，供应商列表不能为空");
    ErrorCode SUBJECT_NULL = new ErrorCode(1_004_003_20, "未找到对应科目");
    ErrorCode FORM_CHANGE_MUL = new ErrorCode(1_004_003_019,"配置项重复");
    ErrorCode COMPANY_NOT_MATCH = new ErrorCode(1_004_003_020,"预付主体不一致，请重新选择");
    ErrorCode REPAY_AMOUNT_ERROR = new ErrorCode(1_004_003_021,"还款金额大于待还金额，请确认");
    ErrorCode TOTAL_AMOUNT_NULL = new ErrorCode(1_004_003_021,"报销总额不能为空");
    ErrorCode CURRENCY_ERROR = new ErrorCode(1_004_003_021,"币种不一致，请检查数据");
    ErrorCode ACTUAL_USER_NOT_EXISTS = new ErrorCode(1_004_003_022,"实际报销人不存在");
    ErrorCode APPLY_NOT_EXISTS = new ErrorCode(1_004_003_022,"申请表不存在");
    ErrorCode APPLY_TYPE_NOT_EXISTS = new ErrorCode(1_004_003_023,"申请表类型不存在");
    ErrorCode LOAN_APP_AMOUNT_IS_NULL = new ErrorCode(1_004_003_024,"借款金额为空");
    ErrorCode LOAN_APP_CURRENCY_IS_NULL = new ErrorCode(1_004_003_025,"借款币别为空");
    ErrorCode REIMB_IS_PAYMENT = new ErrorCode(1_004_003_026,"该报销申请单对应的付款单已支付，不可作废");
    ErrorCode REIMB_TYPE_NOT_EXISTS = new ErrorCode(1_004_003_027,"报销类型不存在-{}");


    ErrorCode CREATOR_IS_NULL = new ErrorCode(1_004_004_001, "创建人不能为空");
}
