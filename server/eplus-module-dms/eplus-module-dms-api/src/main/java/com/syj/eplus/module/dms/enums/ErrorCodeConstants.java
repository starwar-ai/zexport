package com.syj.eplus.module.dms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * crm 错误码枚举类
 * <p>
 * crm 系统，使用 1_008_001_001 段
 */
public interface ErrorCodeConstants {

    // ========== 财务管理 1_008_001_001 ==========
    ErrorCode FORWARDER_COMPANY_INFO_NOT_EXISTS = new ErrorCode(1_010_001_001, "船代公司不存在");
    ErrorCode SHIPMENT_PLAN_NOT_EXISTS = new ErrorCode(1_010_001_002, "出运计划不存在");
    ErrorCode TRANSFORM_SHIPMENT_PLAN_ERR = new ErrorCode(1_010_001_003, "转出运计划出错-{}");
    ErrorCode COMMODITY_INSPECTION_NOT_EXISTS = new ErrorCode(1_010_001_004, "商检单不存在");
    ErrorCode DECLARATION_NOT_EXISTS = new ErrorCode(1_010_001_005, "报关单不存在");
    ErrorCode SETTLEMENT_FORM_NOT_EXISTS = new ErrorCode(1_010_001_006, "结汇单不存在");
    ErrorCode SHIPMENT_NOT_EXISTS = new ErrorCode(1_010_001_006, "出运单不存在");
    ErrorCode CONTRACT_ZERO_VOLUME = new ErrorCode(1_010_001_007, "总体积不能为0");
    ErrorCode CURRENCY_ITEM_OUTERBOX_NUM = new ErrorCode(1_010_001_008, "产品-{}外箱装量不可为0");
    ErrorCode CURRENCY_ITEM_BOX_COUNT = new ErrorCode(1_010_001_009, "箱数计算异常-{}");
    ErrorCode CONTRACT_ITEM_ITEM_COMMISSION = new ErrorCode(1_010_001_010, "明细佣金金额计算异常-{}");
    ErrorCode CONTRACT_TOTAL_VAT_REFUND = new ErrorCode(1_010_001_011, "退税总额计算异常-{}");
    ErrorCode CONTRACT_TOTAL_GOODS_VALUE = new ErrorCode(1_010_001_012, "货值合计计算异常-{}");

    ErrorCode DECLARATION_ITEM_NOT_EXISTS = new ErrorCode(1_010_001_013, "报关单明细不存在");
    ErrorCode CONTRACT_TOTAL_GOODS_QUANTITY = new ErrorCode(1_010_001_013, "数量合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_GROUSSWEIGHT = new ErrorCode(1_010_001_014, "毛重合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_WEIGHT = new ErrorCode(1_010_001_015, "净重合计计算异常-{}");
    ErrorCode SHIPMENT_ITEM_ALL_CONVERTED = new ErrorCode(1_010_001_016, "出运明细已转拉柜通知单");

    ErrorCode CONTRACT_TOTAL_VOLUME = new ErrorCode(1_010_001_016, "体积合计计算异常-{}");
    ErrorCode SHIPMENT_PLAN_ITEM_STATUS_EMPTY = new ErrorCode(1_010_001_017, "出运计划明细状态为空");
    ErrorCode SHIPMENT_VENDER_CODE_EMPTY = new ErrorCode(1_010_001_018, "出运单明细中供应商为空");
    ErrorCode SHIPMENT_PURCHASE_CONTRACT_CODE_EMPTY = new ErrorCode(1_010_001_019, "出运单明细中采购合同号为空");

    ErrorCode CONTRACT_TOTAL_DECLARATION = new ErrorCode(1_010_001_018, "报关合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_PURCHASE = new ErrorCode(1_010_001_019, "采购合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_COMMISSIONAMOUNT = new ErrorCode(1_010_001_020, "佣金合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_INSURANCEFEE = new ErrorCode(1_010_001_021, "保险费合计计算异常-{}");
    ErrorCode SHIPMENT_FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_010_001_022, "未找到出运相关变更字段配置");

    ErrorCode INVALID_TO_DOCUMENT = new ErrorCode(1_010_001_023, "转单失败，存在已转记录信息！");

    ErrorCode FAIL_TO_COMMODITY_INSPECTION = new ErrorCode(1_010_001_023, "转单失败，有产品不需要商检！");
    ErrorCode SALE_CONTRACT_STATUS_EMPTY = new ErrorCode(1_010_001_024, "销售合同状态为空");
    ErrorCode INVALID_TO_DECLARATION = new ErrorCode(1_010_001_025, "报关数量合计不可大于出运数量！");
    ErrorCode ADD_SUB_CANNOT_DELETE = new ErrorCode(1_010_001_026, "已经认领的加减项不能进行删除");
    ErrorCode SALE_CONTRACT_COLLECTION_PLAN_CURRENCY_WRONG = new ErrorCode(1_010_001_027, "收款计划金额币种不一致");
    ErrorCode SALE_CONTRACT_COLLECTION_PLAN_OVER_DEFAULT_PURCHASE = new ErrorCode(1_010_001_028, "该销售合同的收款计划设置了控制采购，在未收到款前，不能下推采购计划");
    ErrorCode SALE_CONTRACT_COLLECTION_PLAN_OVER_DEFAULT_SHIPMENT = new ErrorCode(1_010_001_029, "该销售合同的收款计划设置了出运采购，在未收到款前，不能转出库单");
    ErrorCode FOREIGN_TRADE_COMPANY_NOT_SAME = new ErrorCode(1_010_001_030, "外贸公司不一致，不允许下推");



    ErrorCode CONFIRM_FLAG_ERROR = new ErrorCode(1_011_002_001, "该出运明细的确认状态为已确认，无法确认");
    ErrorCode CONFIRM_SOURCE_NOT_EXISTS = new ErrorCode(1_011_002_002, "该出运明细的确认来源为空，无法确认");
    ErrorCode CONFIRM_SOURCE_NOT_EXISTS2 = new ErrorCode(1_011_002_003, "确认来源为空，无法更新");
    ErrorCode SHIPMENT_CHANGE_NOT_EXISTS = new ErrorCode(1_010_001_022, "出运变更单不存在");
    ErrorCode SHIPMENT_CHANGE_STATUS_EXCEPTION = new ErrorCode(1_010_001_022, "请选择待审批或者已驳回的采购变更单");
    ErrorCode SHIPMENT_ITEM_NOT_EXISTS = new ErrorCode(1_010_001_023, "出运单明细不存在");
    ErrorCode SOURCE_SALE_CONTRACT_CODE_EMPTY = new ErrorCode(1_010_001_024, "来源销售合同编号为空");
    ErrorCode SALE_CONTRACT_CODE_EMPTY = new ErrorCode(1_010_001_024, "销售合同编号为空");
    ErrorCode QUANTITY_NOT_SAME = new ErrorCode(1_010_001_025, "出运数量与库存数量不一致");
    ErrorCode PURCHASE_CONTRACT_NOT_EXISTS = new ErrorCode(1_010_001_026, "采购合同不存在");
    ErrorCode PURCHASE_PLAN_NOT_EXISTS = new ErrorCode(1_010_001_027, "采购计划不存在");
    ErrorCode PURCHASE_PLAN_ITEM_NOT_EXISTS = new ErrorCode(1_010_001_028, "采购计划明细不存在");
    ErrorCode PURCHASE_PLAN_COMBINE_ITEM_NOT_EXISTS = new ErrorCode(1_010_001_029, "采购计划不存在需要加工的产品");
    ErrorCode CHILDREN_STOCK_NOT_EXISTS = new ErrorCode(1_010_001_030, "子库存不存在，请检查");
    ErrorCode STOCK_QUANTITY_NOT_ENOUGH = new ErrorCode(1_010_001_031, "库存数量不足");
    ErrorCode PURCHASE_CONTRACT_IS_NOT_AUXILIARY = new ErrorCode(1_010_001_032, "合同不是包材合同,不能操作");
    ErrorCode PURCHASE_CONTRACT_IS_AUXILIARY = new ErrorCode(1_010_001_033, "合同是包材合同,不能操作");
    ErrorCode SHIPPING_QUANTITY_ZERO = new ErrorCode(1_010_001_034, "出运数量为0");
    ErrorCode DECLARATION_QUANTITY_ZERO = new ErrorCode(1_010_001_034, "报关数量为0");
    ErrorCode QTY_PEROUTERBOX_ZERO = new ErrorCode(1_010_001_034, "外箱装量为0");

    ErrorCode FORWARDER_FEE_NOT_EXISTS = new ErrorCode(1_010_003_001, "船代费用不存在");
    ErrorCode FORWARDER_FEE_CODE_MORE = new ErrorCode(1_010_003_002, "船代费用编号存在多个");
    ErrorCode FORWARDER_FEE_CODE_WRONG = new ErrorCode(1_010_003_001, "船代费用编号列表传参异常");
    ErrorCode FORWARDER_FEE_AMOUNT_NULL = new ErrorCode(1_010_003_002, "均摊金额为0，请检查数据及当前币种的税率");
    ErrorCode SHIPMENT_NOT_EXISTS_SALE_CONTRACT = new ErrorCode(1_010_001_026, "出运明细没有外销合同");
    ErrorCode SHIPMENT_QUANTITY_NOT_ENOUGHT = new ErrorCode(1_010_001_027, "出运数量不足，不可拆分");
    ErrorCode MANY_SHIPMENT_NOT_ALLOWED = new ErrorCode(1_010_001_028, "不允许操作多条出运明细");
    ErrorCode HSDATA_UNIT_NOT_EXISTS = new ErrorCode(1_010_001_029, "海关编码单位字典数据不存在");
    ErrorCode ZZHOUSE_NOT_EXISTS = new ErrorCode(1_010_001_029, "中转仓库不存在");
    ErrorCode UNTURN_SALE_SKU = new ErrorCode(1_010_001_030, "存在相同销售合同相同产品未转出库单信息");
    ErrorCode STOCK_LOCK_NOT_EXISTS = new ErrorCode(1_010_001_031, "未找到锁定库存-{}");
    ErrorCode SKU_NOT_OUT_BILL = new ErrorCode(1_010_001_032, "产品未出库，请联系对应工作人员");
    ErrorCode SKU_OUT_QUANTITY_OUT_OF_SHIPPING_QUANTITY = new ErrorCode(1_010_001_033, "产品出库数量大于出运数量，请联系对应工作人员");
    ErrorCode SALE_CONTRACT_ITEM_EXISTS_PURCHASE = new ErrorCode(1_010_001_034, "销售明细已经存在后续单据，不可删除");
    ErrorCode INVOICE_CODE_EXITED = new ErrorCode(1_010_001_035, "发票号重复使用");
    ErrorCode NOT_MANUFACTURE = new ErrorCode(1_010_001_036, "未拆分采购,无加工信息");
    ErrorCode NOT_ALLOW_CHANGE = new ErrorCode(1_010_001_037, "检测到对应采购合同未全部作废，不允许变更");
    ErrorCode PURCHASE_CONTRACT_ITEM_IS_SPLIT = new ErrorCode(1_010_001_038, "拆分采购不可在出运开票");
    ErrorCode CHILD_SKU_STOCK_NOT_ENOUGHT = new ErrorCode(1_010_001_039, "子产品库存数量不足");
    ErrorCode SHIPMENT_FRONT_SHIPPING_MARK_NOT_SAME = new ErrorCode(1_010_001_040, "出运明细正面唛头不一致");
    ErrorCode SHIPMENT_RECEIVE_PERSON_NOT_SAME = new ErrorCode(1_010_001_041, "出运明细收货人不一致");
    ErrorCode SHIPMENT_NOTIFY_PERSON_NOT_SAME = new ErrorCode(1_010_001_042, "出运明细通知人不一致");
    ErrorCode INVOICING_NOTICES_CLOSE_ERROR = new ErrorCode(1_010_001_043, "该开票通知已经进行登票，不可作废");
    ErrorCode SHIPMENT_QUANTITY_EXCEED_NOT_CHANGE = new ErrorCode(1_010_001_044, "可出运数量大于待转出运数量，不可进行变更");
    ErrorCode PURCHASE_CONTRACT_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_045, "采购合同币种{}跟采购总金额币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_FREIGHT_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_046, "采购合同币种{}跟运费币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_COST_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_047, "采购合同币种{}跟其他费用币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_PAYED_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_048, "采购合同币种{}跟已付款金额币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_PREPAY_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_049, "采购合同币种{}跟预付金额币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_INVOICE_CURRENCY_NOT_MATCHING = new ErrorCode(1_010_001_050, "采购合同币种{}跟登票币种{}不一致");
    ErrorCode PURCHASE_CONTRACT_TRANSFORM_PAYMENT_APPLY = new ErrorCode(1_010_001_051, "已有付款申请单，不能进行变更供应商");
    ErrorCode PURCHASE_CONTRACT_CODE_EXISTS = new ErrorCode(1_010_001_052, "采购合同编号已存在");
    ErrorCode PURCHASE_CONTRACT_AUDIT_STATUS_ERROR = new ErrorCode(1_010_001_053, "审核通过的数据不支持变更编号");
    ErrorCode GEN_SKU_STOCK_NOT_ENOUGH = new ErrorCode(1_010_001_054, "普通产品{}未找到库存");
    ErrorCode SHIPMENT_ID_IS_EMPTY = new ErrorCode(1_010_001_055, "出运单号不存在");
    ErrorCode SHIPMENT_ID_IS_NOT_SINGLE = new ErrorCode(1_010_001_056, "仅允许移除相同出运明细数据");
    ErrorCode SHIPMENT_ITEM_NOT_ALL_DELETED = new ErrorCode(1_010_001_057, "出运单明细不允许全部移除");
    ErrorCode TARGET_SHIPMENT_NOT_EXISTS = new ErrorCode(1_010_001_058, "目标出运明细不存在,请重新选择数据");
    ErrorCode SALE_ITEM_DATA_NOT_FOUND = new ErrorCode(1_010_001_059, "未找到对应销售明细数据,请检查数据");
    ErrorCode SALE_PURCHASE_QUANTITY_NOT_ENOUGH = new ErrorCode(1_010_001_060, "销售采购数量不足");
    ErrorCode PRODUCING_QUANTITY_NOT_ENOUGH = new ErrorCode(1_010_001_061, "库存的在制数量为：{}，减少的数量为{}，不可以变更");
    ErrorCode DECLARATION_INVOICE_NOT_ISSUED = new ErrorCode(1_010_001_062, "报关失败：出运单明细未开票，产品编号：{}");
    ErrorCode DECLARATION_NAME_MISMATCH_INVOICE = new ErrorCode(1_010_001_063, "报关失败：产品[{}]的报关品名[{}]与开票通知中的品名[{}]不一致");

}
