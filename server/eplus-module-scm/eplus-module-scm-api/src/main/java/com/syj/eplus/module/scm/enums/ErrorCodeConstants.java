package com.syj.eplus.module.scm.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 参数配置 1-001-001-001 ==========
    ErrorCode VENDER_NOT_EXISTS = new ErrorCode(1_003_001_001, "供应商信息不存在-{}");

    ErrorCode VENDER_POC_NOT_EXISTS = new ErrorCode(1_003_001_002, "供应商联系人不存在");

    ErrorCode VENDER_MANAGER_NOT_EXISTS = new ErrorCode(1_003_001_003, "供应商采购经理不存在");
    ErrorCode VENDER_BANKACCOUNT_NOT_EXISTS = new ErrorCode(1_003_001_004, "供应商银行账户不存在");

    ErrorCode VENDER_IS_EXISTS = new ErrorCode(1_003_001_005, "供应商编号已经存在");
    ErrorCode VENDER_CURRENCY_RATE_NOT_EXISTS = new ErrorCode(1_003_001_006, "供应商币种不存在");


    ErrorCode CONVERT_VENDER_FAIL = new ErrorCode(1_003_001_009, "转正式供应商失败");
    ErrorCode CREATE_VENDER_FAIL = new ErrorCode(1_003_001_010, "新增供应商失败");
    ErrorCode UPDATE_VENDER_FAIL = new ErrorCode(1_003_001_011, "修改供应商失败");
    ErrorCode QUOTE_NOT_EXISTS = new ErrorCode(1_003_001_012, "供应商报价不存在");
    ErrorCode QUOTE_ITEM_NOT_EXISTS = new ErrorCode(1_003_001_013, "供应商报价明细不存在");
    ErrorCode QUOTE_ITEM_NOT_EXISTS_DEFAULT = new ErrorCode(1_003_001_013, "供应商报价明细不存在默认报价");
    ErrorCode QUOTE_ITEM_EXISTS_MORE_DEFAULT = new ErrorCode(1_003_001_013, "供应商报价明细存在多个默认报价");


    ErrorCode PURCHASE_CONTRACT_NOT_EXISTS = new ErrorCode(1_003_002_001, "采购合同不存在");
    ErrorCode PURCHASE_PLAN_ITEM_NOT_EXISTS = new ErrorCode(1_003_002_002, "采购计划明细不存在");
    ErrorCode PURCHASE_PLAN_ITEM_NEEDQUANTITY_ERROR = new ErrorCode(1_003_002_003, "采购计划明细拆分数量错误");
    ErrorCode PURCHASE_PLAN_ITEM_SUB_EMPTY = new ErrorCode(1_003_002_003, "采购计划明细无子产品");
    ErrorCode PURCHASE_PLAN_NOT_EXISTS = new ErrorCode(1_003_002_003, "采购计划不存在");
    ErrorCode PURCHASE_CONTRACT_ITEM_NOT_EXISTS = new ErrorCode(1_003_002_004, "采购合同明细不存在");
    ErrorCode PURCHASE_PLAN_CREATE_FAIL = new ErrorCode(1_003_002_005, "采购计划创建失败");
    ErrorCode PURCHASE_PLAN_PRODUCT_EMPTY = new ErrorCode(1_003_002_006, "采购计划-商品为空");
    ErrorCode PURCHASE_PLAN_USER_WRONG = new ErrorCode(1_003_002_007, "采购计划-用户数据错误");
    ErrorCode INVALID_BANK_ACCOUNT = new ErrorCode(1_003_002_008, "无效的银行账户");
    ErrorCode PURCHASE_PLAN_PRODUCT_NULL = new ErrorCode(1_003_002_009, "产品列表不能为空");
    ErrorCode PURCHASE_CONTRACT_ITEM_SHIPMENTED = new ErrorCode(1_003_002_010, "采购合同明细已出运-{}!");
    ErrorCode PURCHASE_ITEM_QUANTITY_NOT_ENOUGH = new ErrorCode(1_003_002_011, "采购合同的产品已经不足登票数量，请检查数据问题!");

    ErrorCode PURCHASE_PLAN_CURRENCY_WRONG = new ErrorCode(1_003_002_012, "采购币种不属于当前内部公司");
    ErrorCode PURCHASE_PLAN_LOCK_MORE = new ErrorCode(1_003_002_013, "采购计划锁定数量大于销售数量");
    ErrorCode PURCHASE_PLAN_LOCK_LESS = new ErrorCode(1_003_002_014, "采购计划锁定数量小于销售锁定数量");
    ErrorCode PURCHASE_PLAN_CHECK_COLLECTION_PLAN = new ErrorCode(1_003_002_015, "销售合同付款金额小于预设差异比例，不允许下推采购合同");
    ErrorCode PURCHASE_ITEM_NOT_EXISTS_BY_CODE = new ErrorCode(1_003_002_016, "未查询到开票对应的采购明细");
    ErrorCode PURCHASE_ITEM_MORE_EXISTS_BY_CODE = new ErrorCode(1_003_002_017, "开票对应的采购明细存在多条数据");

    ErrorCode UNDERREVIEW_EDITTING_NOT_ALLOWED = new ErrorCode(1_003_003_001, "审核中,禁止修改");

    ErrorCode VENDER_TRUN_ON = new ErrorCode(1_003_002_008, "供应商已启用");
    ErrorCode VENDER_TRUN_OFF = new ErrorCode(1_003_002_009, "供应商已禁用");

    ErrorCode PURCHASE_CONTRACT_USER_WRONG = new ErrorCode(1_003_002_010, "采购合同-用户数据错误");
    ErrorCode NOT_UPDATE_PROCESS = new ErrorCode(1_003_002_011, "采购-审核中不能编辑");

    ErrorCode PURCHASE_CONTRACT_CHANGE_NOT_EXISTS = new ErrorCode(1_003_002_012, "采购合同变更单不存在");

    ErrorCode PURCHASE_CONTRACT_MESSAGE_NOT_EXISTS = new ErrorCode(1_003_002_013, "采购合同信息不完整，不允提交审核");

    ErrorCode PURCHASE_CONTRACT_DATE_NULL = new ErrorCode(1_003_002_014, "时间为空，无法完成设置");

    ErrorCode PURCHASE_PLAN_ITEM_WRONG = new ErrorCode(1_003_002_015, "转换ID传参异常");

    ErrorCode PURCHASE_PLAN_ITEM_NULL = new ErrorCode(1_003_002_016, "无可转明细");

    ErrorCode PURCHASE_PLAN_TO_CONTRACT_WRONG = new ErrorCode(1_003_002_017, "生成合同数据异常，请检查数据");

    ErrorCode PURCHASE_CONTRACT_IN_STOCK = new ErrorCode(1_003_002_018, "采购合同入供应商在制库失败，请检查");

    ErrorCode PURCHASE_CONTRACT_WAREHOUSE_NULL = new ErrorCode(1_003_002_019, "仓库不存在，请检查");

    ErrorCode PURCHASE_PLAN_ITEM_LOCK_QUANTITY_ERROR = new ErrorCode(1_003_002_020, "锁定数量不可大于销售数量");

    ErrorCode PURCHASE_PLAN_COMPANY_NO_VENDER_ERROR = new ErrorCode(1_003_002_021, "可加工企业无内部供应商");
    ErrorCode PURCHASE_PLAN_QUANTITY_ZERO = new ErrorCode(1_003_002_022, "采购总数量为0不允许下推");

    ErrorCode PURCHASE_PLAN_ITEM_LOCK_EXCEED = new ErrorCode(1_003_002_023, "计划明细：{}锁定数量超出采购数量，请检查！");
    ErrorCode PURCHASE_AMOUNT_ERROR = new ErrorCode(1_003_002_024, "采购金额异常");
    ErrorCode PAYMENT_AMOUNT_ERROR = new ErrorCode(1_003_002_025, "支付金额异常");
    ErrorCode PAYMENT_CURRENCY_ERROR = new ErrorCode(1_003_002_026, "支付币种不一致");
    ErrorCode PAYMENT_AMOUNT_PAYMENT_ERROR = new ErrorCode(1_003_002_027, "支付总金额超过合同总金额");
    ErrorCode PAYMENT_NULL = new ErrorCode(1_003_002_028, "支付方式未找到");

    ErrorCode PURCHASE_CONTRACT_NOT_DELIVERY = new ErrorCode(1_003_003_001, "采购合同非待到货状态");

    ErrorCode PURCHASE_CONTRACT_NOT_COMPLETED = new ErrorCode(1_003_003_002, "采购合同未生产完成");

    ErrorCode PURCHASE_CONTRACT_ID_NULL = new ErrorCode(1_003_004_001, "采购合同id为空");

    ErrorCode REPORTCODE_NULL = new ErrorCode(1_003_004_002, "打印模板编号为空");

    ErrorCode SOURCECODE_NULL = new ErrorCode(1_003_004_003, "来源编号为空");

    ErrorCode SOURCETYPE_NULL = new ErrorCode(1_003_004_004, "外部模板类型为空");

    ErrorCode REPORT_NULL = new ErrorCode(1_003_004_005, "对应模板未查询到{}");

    ErrorCode PURCHASECONTRACT_NULL = new ErrorCode(1_003_004_006, "采购合同未查询到");

    ErrorCode PURCHASECONTRACTITEM_NULL = new ErrorCode(1_003_004_006, "采购合同明细未查询到");
    ErrorCode SKU_PICTURE_NULL = new ErrorCode(1_003_004_007, "采购合同明细产品图片未查询到");
    ErrorCode SKU_MAIN_PICTURE_NULL = new ErrorCode(1_003_004_008, "采购合同明细产品主图未查询到");
    ErrorCode REPORT_NOT_APPROVE = new ErrorCode(1_003_004_009, "模板只有审核通过才可使用");
    ErrorCode PURCHASE_CURRENCY_WRONG = new ErrorCode(1_004_004_010, "采购合同币种不一致");

    ErrorCode BANK_POC_BELONG_VENDER = new ErrorCode(1_003_004_011, "同一银行抬头属于多个供应商");
    ErrorCode INVOICING_NOTICES_NOT_EXISTS = new ErrorCode(1_003_004_012, "开票通知不存在");
    ErrorCode PLEASE_SELECT_MERCHANDISER = new ErrorCode(1_003_004_013, "请选择跟单员");
    ErrorCode PURCHASE_PLAN_EMPTY = new ErrorCode(1_003_004_014, "采购计划为空");
    ErrorCode PURCHASE_USER_EMPTY = new ErrorCode(1_003_004_015, "采购员为空");
    ErrorCode PURCHASE_VENDER_EMPTY = new ErrorCode(1_003_004_016, "供应商为空");
    ErrorCode PURCHASE_PAYMENT_EMPTY = new ErrorCode(1_003_004_017, "付款方式为空");
    ErrorCode PURCHASE_TAX_EMPTY = new ErrorCode(1_003_004_018, "发票类型为空");
    ErrorCode PURCHASE_REGISTRATION_NOT_EXISTS = new ErrorCode(1_003_004_019, "采购登记不存在");
    ErrorCode PURCHASE_REGISTRATION_INPUT_USER_EMPTY = new ErrorCode(1_003_004_026, "发票登记的录入人信息为空");
    ErrorCode PURCHASE_NOT_SIGN_BACK_NOT = new ErrorCode(1_003_004_020, "采购合同未回签");
    ErrorCode PURCHASE_ALL_READY_ORDER = new ErrorCode(1_003_004_021, "采购合同已下单");
    ErrorCode REGISTRATION_ITEM_QUANTITY_WRONG = new ErrorCode(1_003_004_022, "采购登记登票数大于通知单数量");
    ErrorCode PURCHASE_COMPANY_INFO_EMPTY = new ErrorCode(1_003_004_023, "下单主体为空");
    ErrorCode PURCHASE_WITH_TAX_PRICE_NULL = new ErrorCode(1_003_004_024, "采购信息产品单价为空");
    ErrorCode SKU_THUMBNAIL_NULL = new ErrorCode(1_003_004_025, "采购合同明细产品缩略图未查询到");


    ErrorCode INVOICING_NOTICES_ITEM_NOT_EXISTS = new ErrorCode(1_003_004_012, "开票通知明细不存在");
    ErrorCode PAYMENT_APPLY_NOT_EXISTS = new ErrorCode(1_003_005_001, "付款申请不存在");
    ErrorCode PAYMENT_APPLY_ITEM_NOT_EXISTS = new ErrorCode(1_003_005_002, "付款申请子表不存在");
    ErrorCode ITEM_PURCHASE_CONTRACT_ITEM_NOT_EXISTS = new ErrorCode(1_003_005_003, "未找到销售明细对应采购合同,key:{}");
    ErrorCode NOT_SUPPORTED_PAYMENTS_DIFFERENT_VENDER = new ErrorCode(1_003_005_004, "不支持不同供应商付款申请");
    ErrorCode PAYMENT_VENDER_CODE_EMPTY = new ErrorCode(1_003_005_005, "应付供应商编号为空");
    ErrorCode PAYMENT_VENDER_NOT_EXISTS = new ErrorCode(1_003_005_006, "应付供应商不存在");
    ErrorCode PURCHASE_CONTRACT_PAYMENT_EMPTY = new ErrorCode(1_003_005_007, "采购合同{}付款方式为空");
    ErrorCode PURCHASE_COMPANY_EMPTY = new ErrorCode(1_003_005_008, "采购合同{}付款主体为空");
    ErrorCode PURCHASE_PAYMENT_PLAN_EMPTY = new ErrorCode(1_003_005_009, "采购合同{}付款计划为空");
    ErrorCode PAYMENT_PLAN_RECEIVABLE_AMOUNT_EMPTY = new ErrorCode(1_003_005_010, "付款计划{}应付金额为空");
    ErrorCode PURCHASE_CONTRACT_TOTAL_AMOUNT_EMPTY = new ErrorCode(1_003_005_011, "采购总金额为空{}");
    ErrorCode PURCHASE_CONTRACT_ITEM_WITH_TAX_PRICE_EMPTY = new ErrorCode(1_003_005_012, "采购明细含税总价为空{}");
    ErrorCode PURCHASE_CONTRACT_ITEM_QUANTITY_EMPTY = new ErrorCode(1_003_005_012, "采购明细数量为空{}");
    ErrorCode PURCHASE_CONTRACT_RATIO_EMPTY = new ErrorCode(1_003_005_013, "未找到对应合同付款比例{}");
    ErrorCode PURCHASE_CONTRACT_PAYMENT_RATIO_EMPTY = new ErrorCode(1_003_005_013, "采购合同付款比例为空{}");
    ErrorCode RELATION_ORDER_EXCEPTION = new ErrorCode(1_003_005_014, "关联单据计算异常");
    ErrorCode PURCHASE_CONTRACT_NOT_PLACE_ORDER = new ErrorCode(1_003_005_014, "订单未下单不能回签");

    ErrorCode PURCHASE_COMPANY_NOT_EXISTS = new ErrorCode(1_003_005_015, "采购合同主体不存在");
    ErrorCode SALE_CONTRACT_ID_NULL = new ErrorCode(1_003_006_001, "销售合同id为空");
    ErrorCode SALECONTRACT_NULL = new ErrorCode(1_003_006_004, "销售合同未查询到");
    ErrorCode SALECONTRACTITEM_NULL = new ErrorCode(1_003_004_005, "销售合同明细未查询到");
    ErrorCode PAYMENT_PLAN_EMPTY = new ErrorCode(1_003_004_006, "付款申请单付款计划为空{}");
    ErrorCode QUALIFICATION_NOT_EXISTS = new ErrorCode(1_003_004_007, "资质不存在");
    ErrorCode SALE_COMPANY_PATH_EMPTY = new ErrorCode(1_003_004_008, "销售合同订单路径为空");
    ErrorCode SALE_ITEM_UNIT_PRICE_ZERO = new ErrorCode(1_003_004_009, "销售明细单价为 0");

    ErrorCode CHANGE_NOT_ALLOWED = new ErrorCode(1_004_001_001, "已经有高版本供应商信息，无法变更");

    ErrorCode IN_CHANGE_NOT_ALLOWED = new ErrorCode(1_004_001_002, "供应商信息正在变更中，无法变更");

    ErrorCode UNAPPROVE_EDIT_DEL_NOT_ALLOWED = new ErrorCode(1_004_001_003, "只有已通过的可以修改删除");

    ErrorCode FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_004_001_004, "未找到相关表变更字段的配置！");
    ErrorCode VENDER_BANK_ACCOUNT_EXISTS = new ErrorCode(1_004_001_005, "供应商银行账户已存在-{}！");

    ErrorCode STOCKNOTICE_NOT_EXISTS = new ErrorCode(1_003_004_001, "出库单不存在");
    ErrorCode STOCKNOTICE_ITEM_NOT_EXISTS = new ErrorCode(1_003_004_001, "出库单明细不存在");
    ErrorCode PURCHASE_AUXILIARY_ALLOCATION_NOT_EXISTS = new ErrorCode(1_004_001_001, "采购合同辅料分摊不存在");
    ErrorCode PURCHASE_TOTAL_PRICE_NULL = new ErrorCode(1_004_001_002, "采购合同总单价异常，请检查数据");
    ErrorCode PURCHASE_UPDATE_PARA_WRONG = new ErrorCode(1_004_001_003, "采购分摊传参异常");

    ErrorCode CONCESSION_RELEASE_NOT_EXISTS = new ErrorCode(1_004_002_001, "让步放行不存在");
    ErrorCode INVOICE_ITEM_SOURCE_CODE_EMPTY = new ErrorCode(1_004_002_002, "开票通知对应的销售合同明细id为空");

    ErrorCode SALE_CONTRACT_ITEM_NOT_EXISTS = new ErrorCode(1_004_002_003, "销售合同明细不存在");
    ErrorCode PURCHASE_PAYMENT_EXCEPTION = new ErrorCode(1_004_002_004, "未查询到采购合同对应付款计划");

    ErrorCode SALE_CONTRACT_SHIPMENT_QUANTITY_OVER = new ErrorCode(1_004_002_005, "销售合同明细回写已转出运数量异常，超出销售数量");
    ErrorCode PAYMENT_PLAN_NOT_FULL = new ErrorCode(1_004_002_007, "付款计划最后一笔需全额支付");

    ErrorCode NOT_SUPPORTED_PAYMENTS_DIFFERENT_COMPANY = new ErrorCode(1_004_002_008, "不支持不同付款单位合并的付款申请");
    ErrorCode VENDER_NOT_INTERNAL = new ErrorCode(1_004_002_009, "供应商没有内部公司");
    ErrorCode SIGN_BACK_DATE_NOT_NULL = new ErrorCode(1_004_002_010, "回签日期不可为空");
    ErrorCode SIGN_BACK_DATE_NOT_AFTER_NOW = new ErrorCode(1_004_002_011, "回签日期不可大于当前日期");
    ErrorCode PLAN_PURCHASE_CONTRACT_NOT_EXISTS = new ErrorCode(1_004_002_012, "通过计划{}未找到对应采购合同");
    ErrorCode INVOICE_QUANTITY_NOT_ENOUGH = new ErrorCode(1_004_002_013, "采购合同的未开票数量不足,剩余开票数量为{}");
    ErrorCode PURCHASE_ITEM_ALL_INVOICE = new ErrorCode(1_004_002_014, "采购合同明细均已转开票通知");
    ErrorCode TEMPLETE_DOWNLOAD_FAIL = new ErrorCode(1_004_002_015, "模板下载失败{}");
    ErrorCode EXCEL_EXPORT_FAIL = new ErrorCode(1_004_002_016, "EXCEL导出失败");
    ErrorCode PURCHASE_PLAN_NOT_ALLOW_CANCEL = new ErrorCode(1_004_002_017, "存在未作废的拆分采购计划,不允许操作");
    ErrorCode SHIPMENT_PURCHASE_CODE_IS_EMPTY = new ErrorCode(1_004_002_018, "出运单明细中采购合同编号为空");
    ErrorCode CLASE_INVOICE_QUANTITY_ENOUGH = new ErrorCode(1_004_002_019, "作废开票数量大于已开票数量");
    ErrorCode INTERNAL_COMPANY_NOT_EXISTS = new ErrorCode(1_004_002_020, "客户对应的内部公司不存在");
    ErrorCode INTERNAL_COMPANY_VENDER_NOT_EXISTS = new ErrorCode(1_004_002_021, "内部公司对应的供应商不存在");
    ErrorCode SHIPMENT_NOT_EXISTS = new ErrorCode(1_004_002_022, "船代费用对应的出运明细不存在");
    ErrorCode SHIPMENT_FORWARD_FEE_NOT_EXISTS = new ErrorCode(1_004_002_023, "船代费用不存在");
    ErrorCode SALE_CONTRACT_FREE_QUANTITY_ERROR = new ErrorCode(1_004_002_024, "计算销售赠品数量异常");
    ErrorCode PURCHASE_ITEM_ALL_TRANSFER_ORDER = new ErrorCode(1_004_002_025, "采购合同明细已存在未处理开票记录，不可重复转");
    ErrorCode VENDER_NOT_AVAILABLE = new ErrorCode(1_004_002_026, "供应商不可用");
    ErrorCode QUOTE_ITEM_VOLUME_ZERO = new ErrorCode(1_004_002_027, "报价体积为0");
    ErrorCode LOAN_CURRENCY_IS_EMPTY = new ErrorCode(1_004_002_028, "借款币种为空");
    ErrorCode AUXILIARY_PURCHASE_CONTRACT_EXISTS = new ErrorCode(1_004_002_029, "该合同已经转过付款申请，不可作废！！！");
    ErrorCode NOT_CHANGE_VENDER_NAME = new ErrorCode(1_004_002_030, "该合同已经转过付款申请，不可变更供应商名称！！！");
    ErrorCode NOT_CHANGE_PURCHASE_QUANTITY = new ErrorCode(1_004_002_031, "该合同已经转过付款申请，不可变更采购数量！！！");
    
    ErrorCode PURCHASE_TYPE_NULL = new ErrorCode(1_004_002_032, "开票通知审批流程缺少采购类型，无法提交审批");
    ErrorCode SALE_TYPE_NULL = new ErrorCode(1_004_002_033, "开票通知审批流程缺少销售类型，无法提交审批");
    ErrorCode STOCK_NOTICE_EXISTS_BILL = new ErrorCode(1_004_002_034, "通知单已存在后续单据,禁止作废");
    ErrorCode STOCK_NOTICE_NOT_ALLOW_CANCEL = new ErrorCode(1_004_002_035, "只允许作废手动创建的通知单");
    ErrorCode SPLIT_PURCHASE_NEED_SHIPMENT = new ErrorCode(1_004_002_036, "拆分采购的明细需确认出运才可转开票");
    ErrorCode VENDER_TAX_MSG_IS_EMPTY = new ErrorCode(1_004_002_037, "供应商{}财务信息为空");
    ErrorCode VENDER_TAX_MSG_IS_NOT_DEFAULT = new ErrorCode(1_004_002_038, "供应商{}未找到默认财务信息");
}
