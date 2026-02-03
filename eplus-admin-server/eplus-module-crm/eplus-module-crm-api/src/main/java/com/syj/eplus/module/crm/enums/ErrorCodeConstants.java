package com.syj.eplus.module.crm.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 参数配置 1-001-001-001 ==========
    ErrorCode CUST_NOT_EXISTS = new ErrorCode(1_001_001_001, "客户资料不存在%s");

    ErrorCode CUST_ATTACHMENT_NOT_EXISTS = new ErrorCode(1_001_001_002, "客户附件信息不存在");

    ErrorCode CUST_BANKACCOUNT_NOT_EXISTS = new ErrorCode(1_001_001_003, "银行账户信息不存在");

    ErrorCode CUST_MANAGER_NOT_EXISTS = new ErrorCode(1_001_001_004, "客户移交跟进不存在");

    ErrorCode CUST_PICTURE_NOT_EXISTS = new ErrorCode(1_001_001_005, "客户图片信息不存在");

    ErrorCode CUST_POC_NOT_EXISTS = new ErrorCode(1_001_001_006, "客户联系人不存在");

    ErrorCode MARK_NOT_EXISTS = new ErrorCode(1_001_001_007, "唛头不存在");

    ErrorCode UNAPPROVE_EDIT_DEL_NOT_ALLOWED = new ErrorCode(1_001_001_008, "只有已通过的可以删除旧数据");

    ErrorCode CUST_SETTLEMENT_NOT_EXISTS = new ErrorCode(1_001_001_009, "结汇方式不存在");

    ErrorCode EMPTY_CODE = new ErrorCode(1_001_001_010, "客户编码获取为空");
    ErrorCode CONVERT_CUST_FAIL = new ErrorCode(1_001_001_011, "转正式客户失败");
    ErrorCode CREATE_CUST_FAIL = new ErrorCode(1_001_001_012, "创建客户失败");
    ErrorCode UPDATE_CUST_FAIL = new ErrorCode(1_001_001_013, "修改客户失败");

    ErrorCode UNSUBMIT_EDIT_DEL_NOT_ALLOWED = new ErrorCode(1_001_001_014, "只有提交中的可以修改删除");

    ErrorCode CHANGE_NOT_ALLOWED = new ErrorCode(1_001_001_015, "已经有高版本客户信息，无法变更");

    ErrorCode IN_CHANGE_NOT_ALLOWED = new ErrorCode(1_001_001_016, "客户信息正在变更中，无法变更");


    ErrorCode CLUE_NOT_EXISTS = new ErrorCode(1_001_002_001, "潜在客户资料不存在");

    ErrorCode CLUE_ATTACHMENT_NOT_EXISTS = new ErrorCode(1_001_002_002, "潜在客户附件信息不存在");

    ErrorCode CLUE_MANAGER_NOT_EXISTS = new ErrorCode(1_001_002_004, "潜在客户移交跟进不存在");

    ErrorCode CLUE_PICTURE_NOT_EXISTS = new ErrorCode(1_001_002_005, "潜在客户图片信息不存在");

    ErrorCode CLUE_POC_NOT_EXISTS = new ErrorCode(1_001_002_006, "潜在客户联系人不存在");
    ErrorCode CLUE_TRUN_ON = new ErrorCode(1_001_002_007, "客户已启用");
    ErrorCode CLUE_TRUN_OFF = new ErrorCode(1_001_002_008, "客户已禁用");
    ErrorCode BANK_POC_BELONG_CUST = new ErrorCode(1_001_002_008, "同一银行抬头属于多个客户");
    ErrorCode NOT_UPDATE_PROCESS = new ErrorCode(1_001_002_009, "客户-审核中不能编辑");

    ErrorCode INVALID_TO_DOCUMENT = new ErrorCode(1_001_003_001, "转单失败，存在已转记录信息！");
    ErrorCode INVALID_TO_DECLARATION = new ErrorCode(1_001_003_002, "报关数量合计不可大于报关总数量！");

    ErrorCode INVALID_PAYMENT_APPLY_ITEM = new ErrorCode(1_001_003_003, "未找到采购合同对应明细！");

    ErrorCode FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_004_001, "未找到相关表变更字段的配置！");
    ErrorCode CUST_NAME_EXISTS = new ErrorCode(1_001_004_002, "客户名称已经存在！");
    ErrorCode VENDER_NAME_EXISTS = new ErrorCode(1_001_004_003, "供应商名称已经存在！");
    ErrorCode CURRENCY_WRONG = new ErrorCode(1_001_004_004, "币种数据异常！");
    ErrorCode ANTI_AUDIT_EXCEPT = new ErrorCode(1_001_004_005, "已存在下游单据,禁止操作");


    ErrorCode COLLECTION_ACCOUNT_NOT_EXISTS = new ErrorCode(1_001_005_001, "收款账号不存在");

    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1_001_006_001, "客户分类不存在");

    ErrorCode CATEGORY_NOT_EMPTY = new ErrorCode(1_001_006_002, "客户分类等级不可为空");
    ErrorCode CATEGORY_NOT_EXCEED_THREE = new ErrorCode(1_001_006_003, "客户分类等级不可超过三级");

    ErrorCode LASTID_IO_FAIL = new ErrorCode(1_001_007_002, "最新编号读写错误");

    ErrorCode FIND_SIMILAR_FAIL = new ErrorCode(1_001_007_003, "查找相似客户名称失败");
    ErrorCode PAYMENT_APPLY_IS_EXISTS = new ErrorCode(1_001_007_004, "已存在未支付的付款申请单,不可重复创建");
    ErrorCode PAYMENT_PAID_NOT_CLOSE = new ErrorCode(1_001_007_005, "已经支付的付款单不可作废");
    ErrorCode FACTORY_AREA_NOT_NULL = new ErrorCode(1_001_007_006, "工厂地址不可为空");

    ErrorCode VENDER_ERROR = new ErrorCode(1_001_004_007, "未找到供应商信息");
    ErrorCode INNER_VENDER_PRODUCT_EXCEPTION = new ErrorCode(1_001_004_008, "内部供应商组合产品不可组合采购");
    ErrorCode DOCUMENTER_IS_EMPTY = new ErrorCode(1_001_004_009, "单证员不存在");
    ErrorCode MODEL_TEMPLETE_NOT_EXISTS = new ErrorCode(1_001_004_010, "模板未找到-{}");
    ErrorCode INVALID_COMPANYID = new ErrorCode(1_001_003_001, "转单失败,存在订单路径不包含下单主体记录信息！");
    ErrorCode BASIC_SKU_CODE_IS_NULL = new ErrorCode(1_001_003_002, "产品code-{}基础产品编号为空");
    ErrorCode SHIPMENT_COMPANY_IS_TO_SETTLEFORM = new ErrorCode(1_001_003_003, "已转结汇单");
    ErrorCode SKU_NOT_APPROVED = new ErrorCode(1_001_003_004, "选中该产品还未审批通过，请先提交审批");
    ErrorCode SKU_QUOTE_VENDER_NOT_APPROVED = new ErrorCode(1_001_003_005, "选中该产品的供应商报价中的供应商还未从潜在供应商转出或者未审批通过，请先将基础信息补齐");
    ErrorCode SKU_SUB_QUOTE_VENDER_NOT_APPROVED = new ErrorCode(1_001_003_006, "选中该组合产品的子产品供应商报价中的供应商还未从潜在供应商转出或者未审批通过，请先将基础信息补齐");
    ErrorCode SKU_SUB_NOT_APPROVED = new ErrorCode(1_001_003_007, "选中该组合产品的子产品还未审批通过，请先提交审批");
    ErrorCode SHIPMENT_NOT_ALLOW_EXPORT_MULTI_COMPANY = new ErrorCode(1_001_003_008, "存在不同外贸公司转出运,请检查数据");
    ErrorCode SETTLEMENT_DIFF_NOT_TRANSFORM = new ErrorCode(1_001_003_009, "结汇方式不同,不可合并转结汇单");
    ErrorCode TRANSPORT_DIFF_NOT_TRANSFORM = new ErrorCode(1_001_003_010, "运输方式不同,不可合并转结汇单");
    ErrorCode SHIPMENT_BATCH_FLAG = new ErrorCode(1_001_003_011, "出运明细已分批出运");
    ErrorCode SHIPMENT_NOT_ALLOW_BATCH_FLAG = new ErrorCode(1_001_003_012, "已有后续单据,不可分批出运");
    ErrorCode DOCUMENTER_NOT_FUND = new ErrorCode(1_001_003_013, "未找到出运明细{}单证员");
    ErrorCode ONLY_SUPPORTS_BATCH_OPERATION_EXECUTION_DATA = new ErrorCode(1_001_003_014, "仅支持批量操作处理中数据,{}");
    ErrorCode SHIPPING_QUANTITY_NOT_ENOUGH = new ErrorCode(1_001_003_015, "可拆分数量不足,请检查数据");
    ErrorCode INTERNAL_CUST_NOT_EXISTS = new ErrorCode(1_001_003_016, "生成内部单据时，未找到{}对应的内部客户");
    ErrorCode PURCHASE_PLAN_NOT_ALLOW_TO_CONTRACT = new ErrorCode(1_001_003_017, "该计划已转采购合同");
    ErrorCode CONTRACT_CONVERTED_PURCHASE_PLAN = new ErrorCode(1_001_003_018, "该合同已转采购计划");
    ErrorCode CONTRACT_CONVERTED_SHIPMENT_PLAN = new ErrorCode(1_001_003_019, "该合同已转出运计划");
    ErrorCode CONTRACT_ALL_SIGN_BACK = new ErrorCode(1_001_003_020, "该外销合同已回签");
    ErrorCode CONTRACT_ALL_CLOSE = new ErrorCode(1_001_003_021, "该外销合同已作废");
    ErrorCode SALE_CONTRACT_ITEM_CONVERTED_NOTICE = new ErrorCode(1_001_003_022, "该内销合同已转出库通知单");
    ErrorCode PURCHASE_PLAN_CLOSED = new ErrorCode(1_001_003_023, "该采购计划已作废");
    ErrorCode PURCHASE_CONTRACT_CLOSED = new ErrorCode(1_001_003_024, "该采购合同已作废");
    ErrorCode PURCHASE_CONTRACT_COMPLETED = new ErrorCode(1_001_003_025, "该采购合同已生产完成");
    ErrorCode PURCHASE_CONTRACT_FINISHED = new ErrorCode(1_001_003_026, "该采购合同已整单完成");
    ErrorCode PURCHASE_CONTRACT_CONVERTED_STOCK_NOTICE = new ErrorCode(1_001_003_027, "该采购合同已转入库通知单");
    ErrorCode PURCHASE_PLAN_CONVERTED_NOT_ALLOW_TO_CONTRACT = new ErrorCode(1_001_003_028, "存在已转合同明细,请检查数据");
    ErrorCode PURCHASE_PLAN_COMBINE_NOT_ALLOW_TO_CONTRACT = new ErrorCode(1_001_003_029, "采购主体是国外企业，无法拆分采购！！！");
    ErrorCode NOT_ALLOW_SIM_VENDER_CONVERTED = new ErrorCode(1_001_003_030, "不允许报价是内部供应商的产品下推");
    ErrorCode SALE_CONTRACT_IS_EXISTS = new ErrorCode(1_001_003_031, "已存在销售合同,不可删除");
    ErrorCode PAYMENT_APP_IS_EXISTS = new ErrorCode(1_001_003_032, "已存在费用支付,不可删除");
    ErrorCode FREE_SHARE_IS_EXISTS = new ErrorCode(1_001_003_033, "已存在费用归集,不可删除");
    ErrorCode PURCHASE_CONTRACT_IS_EXISTS = new ErrorCode(1_001_003_034, "已存在采购合同,不可删除");

}
