package com.syj.eplus.module.sms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * CRM 错误码枚举类
 * <p>
 * crm 系统，使用 1-020-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 销售合同 1-020-000-000 ==========
    ErrorCode SALE_CONTRACT_NOT_EXISTS = new ErrorCode(1_009_001_001, "合同不存在");

    ErrorCode CONTRACT_ITEM_NOT_EXISTS = new ErrorCode(1_009_002_001, "合同明细不存在");
    ErrorCode CONTRACT_ITEM_TRAILERFEE = new ErrorCode(1_009_002_002, "明细拖柜费计算异常-{}");
    ErrorCode CONTRACT_TOTAL_TRAILERFEE = new ErrorCode(1_009_002_003, "预估总运费计算异常-{}");
    ErrorCode CONTRACT_ZERO_VOLUME = new ErrorCode(1_009_002_004, "总体积不能为0");
    ErrorCode CONTRACT_ITEM_TOTAL_TRAILERFEE = new ErrorCode(1_009_002_005, "明细预估总费用计算异常-{}");
    ErrorCode CONTRACT_ITEM_ITEM_COMMISSION = new ErrorCode(1_009_002_006, "明细佣金金额计算异常-{}");
    ErrorCode CONTRACT_COMMISSION = new ErrorCode(1_009_002_007, "佣金金额计算异常-{}");
    ErrorCode CONTRACT_BULK_HANDLING_VOLUME = new ErrorCode(1_009_002_008, "散货体积计算异常-{}");
    ErrorCode CONTRACT_TWENTY_FOOT_CABINET_NUM = new ErrorCode(1_009_002_009, "20尺柜数量计算异常-{}");
    ErrorCode CONTRACT_FORTY_FOOT_CABINET_NUM = new ErrorCode(1_009_002_010, "40尺柜计算异常-{}");
    ErrorCode CONTRACT_FORTY_FOOT_CONTAINER_NUM = new ErrorCode(1_009_002_011, "40尺高柜计算异常-{}");
    ErrorCode CONTRACT_PLATFORM_FEE = new ErrorCode(1_009_002_012, "平台费计算异常-{}");
    ErrorCode CONTRACT_TOTAL_FEE = new ErrorCode(1_009_002_012, "销售总金额计算异常-{}");
    ErrorCode CONTRACT_TOTALUSD_FEE = new ErrorCode(1_009_002_012, "销售总金额USD计算异常-{}");
    ErrorCode CONTRACT_ADD_ITEM_FEE = new ErrorCode(1_009_002_013, "加项金额计算异常-{}");
    ErrorCode CONTRACT_SUB_ITEM_FEE = new ErrorCode(1_009_002_014, "减项金额计算异常-{}");
    ErrorCode CONTRACT_TOTAL_GROUSSWEIGHT = new ErrorCode(1_009_002_015, "毛重合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_WEIGHT = new ErrorCode(1_009_002_016, "净重合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_VOLUME = new ErrorCode(1_009_002_017, "体积合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_GOODS_VALUE = new ErrorCode(1_009_002_018, "货值合计计算异常-{}");
    ErrorCode CONTRACT_TOTAL_PURCHASE = new ErrorCode(1_009_002_019, "采购合计计算异常-{}");
    ErrorCode CONTRACT_ITEM_VAT_REFUND = new ErrorCode(1_009_002_021, "明细退税金额计算异常-{}");
    ErrorCode CONTRACT_TOTAL_VAT_REFUND = new ErrorCode(1_009_002_022, "退税总额计算异常-{}");
    ErrorCode CURRENCY_RATE_NOT_EXISTS = new ErrorCode(1_009_002_023, "{}汇率不存在");
    ErrorCode CURRENCY_TOTAL_RECEIVABLE = new ErrorCode(1_009_002_024, "应收汇款计算异常-{}");
    ErrorCode CURRENCY_ITEM_OUTERBOX_NUM = new ErrorCode(1_009_002_025, "产品-{}外箱装量不可为0");
    ErrorCode CURRENCY_ITEM_BOX_COUNT = new ErrorCode(1_009_002_026, "箱数计算异常-{}");
    ErrorCode SALE_CONTRACT_CHANGE_NOT_EXISTS = new ErrorCode(1_009_002_027, "销售合同变更单不存在");
    ErrorCode SALE_CONTRACT_ITEM_PARENT_NOT_EXISTS = new ErrorCode(1_009_002_028, "销售明细中存在未知合同编号");
    ErrorCode SALE_CONTRACT_ITEM_LOCK_QUANTITY_ERROR = new ErrorCode(1_009_002_029, "锁定数量不可大于销售数量");
    ErrorCode SALE_CONTRACT_ITEM_ALL_CONVERTED_SHIPMENT = new ErrorCode(1_009_002_030, "外销合同明细均已转出运计划");
    ErrorCode SALE_CONTRACT_ITEM_LOCK_EXCEED = new ErrorCode(1_009_002_031, "销售明细：{}锁定数量超出销售数量，请检查！");
    ErrorCode SALE_CONTRACT_ITEM_LOCK_WRONG = new ErrorCode(1_009_002_032, "重新分配的数量和原数量总和不一致");

    ErrorCode ONLY_ALLOW_PUSH_SAME_COMPANY = new ErrorCode(1_009_002_033, "只允许下推相同账套销售明细");
    ErrorCode SALE_CONTRACT_STATUS_EMPTY = new ErrorCode(1_009_002_034, "销售合同状态为空");
    ErrorCode PURCHASE_NOT_EXISTS = new ErrorCode(1_009_002_035, "产品{}没有对应的采购合同或还未锁定库存");
    ErrorCode PURCHASE_ITEM_NOT_EXISTS = new ErrorCode(1_009_002_036, "采购合同明细不存在");
    ErrorCode DEPARTURE_PORT_NOT_SAME = new ErrorCode(1_009_002_037, "销售合同出运口岸不一致");
    ErrorCode DESTINATION_PORT_NOT_SAME = new ErrorCode(1_009_002_038, "销售合同目的口岸不一致");
    ErrorCode TRANSPORT_TYPE_NOT_SAME = new ErrorCode(1_009_002_039, "销售合同运输方式不一致");
    ErrorCode ONLY_ALLOW_PUSH_SAME_FOREIGN_COMPANY = new ErrorCode(1_009_002_040, "只允许相同外贸公司的销售合同下推出运计划");
    ErrorCode PARA_CHILDREN_ID_NOT_EXISTS = new ErrorCode(1_009_002_041, "没有可出运的产品");
    ErrorCode CONTRACT_CURRENCY_RATE_INVALID = new ErrorCode(1_009_002_042, "当前币种未找到对应的汇率-{}");



    ErrorCode QUOTATION_NOT_EXISTS = new ErrorCode(1_009_003_001, "报价单信息不存在");
    ErrorCode QUOTATION_ITEM_NOT_EXISTS = new ErrorCode(1_009_003_002, "报价单明细信息不存在");
    ErrorCode OTHER_FEE_NOT_EXISTS = new ErrorCode(1_009_003_003, "其他费用信息不存在");
    ErrorCode CONFIRM_FLAG_ERROR = new ErrorCode(1_009_004_001, "该销售合同的确认状态为已确认，无法确认");
    ErrorCode CONFIRM_SOURCE_NOT_EXISTS = new ErrorCode(1_009_004_002, "该销售合同的确认来源为空，无法确认");
    ErrorCode CONFIRM_SOURCE_NOT_EXISTS2 = new ErrorCode(1_009_004_003, "确认来源为空，无法更新");
    ErrorCode RELATED_NUM_ERROR = new ErrorCode(1_009_004_004, "查询关联单据数量出错");
    ErrorCode SALE_AUXILIARY_ALLOCATION_NOT_EXISTS = new ErrorCode(1_010_001_001, "销售合同辅料分摊不存在");
    ErrorCode CUST_LIST_NOT_EXITS = new ErrorCode(1_010_001_002, "客户列表不存在");
    ErrorCode OUTERBOX_VOLUME_EMPTY = new ErrorCode(1_010_001_003, "外箱体积为空，请检查数据");
    ErrorCode CONTAINER_QUANTITY_RESULT_EMPTY = new ErrorCode(1_010_001_004, "未获取到尺柜数量计算结果");

    ErrorCode SHIPMENT_QUANTITY_EXCEED = new ErrorCode(1_011_001_001, "[销售明细]出运数量不可大于销售数量");
    ErrorCode BOX_COUNT_EMPTY = new ErrorCode(1_011_001_002, "箱数不可为空");
    ErrorCode SALE_CONTRACT_COMPANY_PATH_EMPTY= new ErrorCode(1_011_001_003, "销售合同订单路径为空");
    ErrorCode COMPANY_NOT_EXITS= new ErrorCode(1_011_001_004, "主体不存在");
    ErrorCode DIFFERENT_PATH_EXCEPTION= new ErrorCode(1_011_001_005, "不同路径销售合同不可下推出运计划");
    ErrorCode SHIPMENT_PURCHASE_TOTAL_EXCEPTION= new ErrorCode(1_011_001_006, "出运明细-采购合计计算异常-{}");
    ErrorCode SHIPMENT_GOODS_TOTAL_EXCEPTION= new ErrorCode(1_011_001_007, "出运明细-货值合计计算异常-{}");
    ErrorCode SHIPMENT_TOTAL_QUANTITY_EXCEPTION= new ErrorCode(1_011_001_008, "出运明细-数量合计计算异常");
    ErrorCode SHIPMENT_TOTAL_BOX_QUANTITY_EXCEPTION= new ErrorCode(1_011_001_009, "出运明细-箱数合计计算异常");
    ErrorCode SHIPMENT_TOTAL_VOLUME_EXCEPTION= new ErrorCode(1_011_001_010, "出运明细-体积合计计算异常");
    ErrorCode SHIPMENT_TOTAL_GROSSWEIGHT_EXCEPTION= new ErrorCode(1_011_001_011, "出运明细-毛重合计计算异常");
    ErrorCode SHIPMENT_TOTAL_WEIGHT_EXCEPTION= new ErrorCode(1_011_001_012, "出运明细-净重合计计算异常");
    ErrorCode SHIPMENT_PLAN_TOTAL_WEIGHT_EXCEPTION= new ErrorCode(1_011_001_013, "出运计划-采购合计计算异常-{}");
    ErrorCode SHIPMENT_PLAN_NOT_SALE_ITEM= new ErrorCode(1_011_001_014, "出运计划-销售合同明细未找到");
    ErrorCode SHIPMENT_DIFF_COMPANY_PATH= new ErrorCode(1_011_001_015, "出运明细-存在不同路径销售合同");
    ErrorCode CONTRACT_ALL_NOT_STOCK= new ErrorCode(1_011_001_016, "所选销售合同均无库存");
    ErrorCode CONTRACT_NOT_STOCK= new ErrorCode(1_011_001_017, "所选销售合同无库存-{}");
    ErrorCode CONTRACT_SKU_NOT_STOCK= new ErrorCode(1_011_001_018, "所选销售明细无库存-cskuCode-{},skuCode-{}");
    ErrorCode CONTRACT_SKU_STOCK_NOT_EQUAL= new ErrorCode(1_011_001_019, "产品-{}库存与销售数量不一致");
    ErrorCode COLLECT_PLAN_NOT_EXECUTED= new ErrorCode(1_011_001_020, "收款计划未执行,不可下推采购计划");
    ErrorCode CONTRACT_SKU_STOCK_NOT_ENOUGH= new ErrorCode(1_011_001_021, "所选销售明细库存不足-cskuCode-{},skuCode-{}");
    ErrorCode SHIPMENT_DECLARATION_TOTAL_EXCEPTION= new ErrorCode(1_011_001_022, "出运明细-报关合计计算异常-{}");
    ErrorCode SHIPMENT_PLAN_ITEM_NOT_EXISTS= new ErrorCode(1_011_001_023, "出运明细-明细不存在-{}");
    ErrorCode SALE_CONTRACT_ITEM_UNIT_PRICE_NULL= new ErrorCode(1_011_001_024, "销售明细单价为空");
    ErrorCode RATE_NOT_EXISTS= new ErrorCode(1_011_001_025, "税率信息为空");
    ErrorCode COMPANY_SHORTNAME_NOT_EXISTS= new ErrorCode(1_011_001_026, "内部公司简称未配置");
    ErrorCode SETTLEMENT_TERM_TYPE_NOT_SAME = new ErrorCode(1_011_001_027, "销售合同价格条款不一致");

    ErrorCode FOREIGN_TRADE_COMPANY_NOT_EXITS= new ErrorCode(1_011_001_028, "不存在外贸公司");
    ErrorCode ERROR_UNIQE_EXITS= new ErrorCode(1_011_001_029, "存在唯一编码为空的销售合同明细信息");
    ErrorCode INTERNAL_CUST_NOT_ALL_STOCK= new ErrorCode(1_011_001_030, "检测到客户为内部客户，销售数量必须全部使用库存！");
    ErrorCode SALE_CONTRACT_NOT_FOREIGN= new ErrorCode(1_011_001_031, "外销合同订单路径未找到外贸资质节点");
    ErrorCode CONTRACT_TOTAL_STOCK_COST = new ErrorCode(1_011_001_032, "库存合计计算异常-{}");
    ErrorCode EXISTS_NEST_ORDER = new ErrorCode(1_011_001_033, "已有后续单据，不可变更订单路径");
    ErrorCode COLLECTION_FLAG = new ErrorCode(1_011_001_034, "该合同已有收款，先取消认领后再进行变更收款计划");
    ErrorCode NOT_FOUND_CONTRACT_RATE = new ErrorCode(1_011_001_035, "未找到合同币种汇率date-{}");
    ErrorCode PURCHASE_CONTRACT_CODE_IS_EMPTY = new ErrorCode(1_011_001_036, "采购合同号为空");
    ErrorCode PURCHASE_CONTRACT_NOT_EXISTS_OR_DELETED = new ErrorCode(1_011_001_037, "采购合同不存在或已删除code-{},请重新下推");
    ErrorCode SHIPMENT_PLAN_ERROR = new ErrorCode(1_011_001_038, "采购计划已完成或者出运数量为0");
}
