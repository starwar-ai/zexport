package com.syj.eplus.module.sms.util;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/2 17:45
 */
public class SaleDict {
    /**
     * 编号类型
     */
    public static final String SN_TYPE = "sn_sale_contact";
    /**
     * 编号前缀
     */
    public static final String CODE_PREFIX = "SSC";
    /**
     * 外销流程实例标识
     */
    public static final String PROCESS_DEFINITION_KEY_EXPORT = "sms_export_sale_contact";

    /**
     * 内销流程实例标识
     */
    public static final String PROCESS_DEFINITION_KEY_DOMESTIC = "sms_domestic_sale_contact";

    /**
     * 外币采购流程实例标识
     */
    public static final String PROCESS_DEFINITION_KEY_FACTORY = "sms_factory_sale_contact";
    /**
     * 操作日志标识
     */
    public static final String OPERATOR_EXTS_KEY = "saleContractCode";
    /**
     * 变更单编号类型
     */
    public static final String CHANGE_SN_TYPE = "SN_PURCHASECONTRACT_CHANGE";
    /**
     * 变更单编号前缀
     */
    public static final String CHANGE_CODE_PREFIX = "PCC";
    /**
     * 变更操作记录标识
     */
    public static final String CHANGE_OPERATOR_EXTS_KEY = "changeSaleContractCode";
    /**
     * 变更流程标识
     */
    public static final String PROCESS_DEFINITION_CHANGE_KEY = "sms_sale_contract_change";
    /**
     * 内销变更流程标识
     */
    public static final String DOMESTIC_DEFINITION_CHANGE_KEY = "sms_domestic_sale_contract_change";
    /**
     * 外币采购变更流程标识
     */
    public static final String FACTORY_DEFINITION_CHANGE_KEY = "sms_factory_sale_contract_change";
    /**
     * 创建销售合同变更日志格式化
     */
    public static final String SALE_CHANGE_LOGGER_CREATE_FORMAT = "创建销售合同变更记录【%s】";

    /**
     * 创建销售合同日志格式化
     */
    public static final String SALE_LOGGER_CREATE_FORMAT = "创建销售合同【%s】";

    /**
     * 删除销售合同日志格式化
     */
    public static final String SALE_LOGGER_DELETE_FORMAT = "删除销售合同【%s】";
    /**
     * 销售明细
     */
    public static final String SALE_ITEM_NAME = "销售明细";

    /**
     * 加减项
     */
    public static final String ADD_SUB_ITEM_NAME = "加减项";

    /**
     * 收款计划
     */
    public static final String COLLECTION_PLAN_NAME = "收款计划";
    /**
     * 销售明细日志标识
     */
    public static final String SALE_ITEM_LOGGER_FLAG = "skuCode";

    /**
     * 日志通用标识
     */
    public static final String SALE_LOGGER_COMMON_FLAG = "id";

    /**
     * 回签
     */
    public static final String SIGN_BACK_OPERATOR_MESSAGE = "【回签】 {},【备注】 {}";
    /**
     * 下推出运计划
     */
    public static final String PUSH_OUT_SHIPMENT_PLAN = "【下推出运计划】";
}
