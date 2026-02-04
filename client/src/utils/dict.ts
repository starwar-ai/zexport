/**
 * 数据字典工具类
 */
import { useDictStoreWithOut } from '@/store/modules/dict'
import { ElementPlusInfoType } from '@/types/elementPlus'

const dictStore = useDictStoreWithOut()

/**
 * 获取 dictType 对应的数据字典数组
 *
 * @param dictType 数据类型
 * @returns {*|Array} 数据字典数组
 */
export interface DictDataType {
  dictType: string
  label: string
  value: any
  colorType: ElementPlusInfoType | ''
  cssClass: string
}

export interface NumberDictDataType extends DictDataType {
  value: number
}

export const getDictOptions = (dictType: string) => {
  return dictStore.getDictByType(dictType) || []
}

export const getIntDictOptions = (dictType: string): NumberDictDataType[] => {
  // 获得通用的 DictDataType 列表
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  // 转换成 number 类型的 NumberDictDataType 类型
  // why 需要特殊转换：避免 IDEA 在 v-for="dict in getIntDictOptions(...)" 时，el-option 的 key 会告警
  const dictOption: NumberDictDataType[] = []
  dictOptions.forEach((dict: DictDataType) => {
    if (dict.value) {
      if (isNaN(Number(dict.value))) {
        dictOption.push({
          ...dict
        })
      } else {
        dictOption.push({
          ...dict,
          value: parseInt(dict.value + '')
        })
      }
    }
  })
  return dictOption
}

export const getStrDictOptions = (dictType: string) => {
  const dictOption: DictDataType[] = []
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  dictOptions.forEach((dict: DictDataType) => {
    dictOption.push({
      ...dict,
      value: dict.value + ''
    })
  })
  return dictOption
}

export const getBoolDictOptions = (dictType: string) => {
  const dictOption: DictDataType[] = []
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  dictOptions.forEach((dict: DictDataType) => {
    dictOption.push({
      ...dict,
      value: dict.value + '' === 'true'
    })
  })
  return dictOption
}

/**
 * 获取指定字典类型的指定值对应的字典对象
 * @param dictType 字典类型
 * @param value 字典值
 * @return DictDataType 字典对象
 */
export const getDictObj = (dictType: string, value: any): DictDataType | undefined => {
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  for (const dict of dictOptions) {
    if (dict.value === value + '') {
      return dict
    }
  }
}

/**
 * 获取指定字典类型的指定值对应的字典对象
 * @param dictType 字典类型
 * @param value 字典值
 * @return DictDataType 字典对象
 */
export const getDictValue = (dictType: string, label: any): string => {
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  const dictValue = ref('')
  dictOptions.forEach((dict: DictDataType) => {
    if (dict.label === label + '') {
      dictValue.value = dict.value
    }
  })
  return dictValue.value
}

/**
 * 获得字典数据的文本展示
 *
 * @param dictType 字典类型
 * @param value 字典数据的值
 * @return 字典名称
 */
export const getDictLabel = (dictType: string, value: any): string => {
  const dictOptions: DictDataType[] = getDictOptions(dictType)
  const dictLabel = ref('')
  dictOptions.forEach((dict: DictDataType) => {
    if (dict.value === value + '') {
      dictLabel.value = dict.label
    }
  })
  return dictLabel.value
}
export const getDictLabels = (dictType: string, value: any): string => {
  if (!value) return '-'
  const labels = value.map((c) => {
    return getDictLabel(dictType, c)
  })
  return labels.join(',')
}
export enum DICT_TYPE {
  USER_TYPE = 'user_type',
  COMMON_STATUS = 'common_status',
  SYSTEM_TENANT_PACKAGE_ID = 'system_tenant_package_id',
  TERMINAL = 'terminal', // 终端

  // ========== SYSTEM 模块 ==========
  SYSTEM_USER_SEX = 'system_user_sex',
  SYSTEM_MENU_TYPE = 'system_menu_type',
  SYSTEM_ROLE_TYPE = 'system_role_type',
  SYSTEM_DATA_SCOPE = 'system_data_scope',
  SYSTEM_NOTICE_TYPE = 'system_notice_type',
  SYSTEM_OPERATE_TYPE = 'system_operate_type',
  SYSTEM_LOGIN_TYPE = 'system_login_type',
  SYSTEM_LOGIN_RESULT = 'system_login_result',
  SYSTEM_SMS_CHANNEL_CODE = 'system_sms_channel_code',
  SYSTEM_SMS_TEMPLATE_TYPE = 'system_sms_template_type',
  SYSTEM_SMS_SEND_STATUS = 'system_sms_send_status',
  SYSTEM_SMS_RECEIVE_STATUS = 'system_sms_receive_status',
  SYSTEM_ERROR_CODE_TYPE = 'system_error_code_type',
  SYSTEM_OAUTH2_GRANT_TYPE = 'system_oauth2_grant_type',
  SYSTEM_MAIL_SEND_STATUS = 'system_mail_send_status',
  SYSTEM_NOTIFY_TEMPLATE_TYPE = 'system_notify_template_type',
  SYSTEM_SOCIAL_TYPE = 'system_social_type',
  DATE_TYPE = 'date_type',
  SETTLEMENT_DATE_TYPE = 'settlement_date_type',
  REPORT_TYPE = 'report_type',
  REPORT_SOURCE_TYPE = 'report_source_type',
  REPORT_BUSINESS_TYPE = 'report_business_type',
  COMPANY_NATURE = 'company_nature', //企业性质
  PROCESSED_FLAG = 'processed_flag', //可加工状态
  PORT_STATUS = 'port_status',
  SUBMIT_STATUS = 'submit_status', //提交状态

  // ========== INFRA 模块 ==========
  INFRA_BOOLEAN_STRING = 'infra_boolean_string',
  INFRA_JOB_STATUS = 'infra_job_status',
  INFRA_JOB_LOG_STATUS = 'infra_job_log_status',
  INFRA_API_ERROR_LOG_PROCESS_STATUS = 'infra_api_error_log_process_status',
  INFRA_CONFIG_TYPE = 'infra_config_type',
  INFRA_CODEGEN_TEMPLATE_TYPE = 'infra_codegen_template_type',
  INFRA_CODEGEN_FRONT_TYPE = 'infra_codegen_front_type',
  INFRA_CODEGEN_SCENE = 'infra_codegen_scene',
  INFRA_FILE_STORAGE = 'infra_file_storage',

  // ========== BPM 模块 ==========
  BPM_MODEL_CATEGORY = 'bpm_model_category',
  BPM_MODEL_FORM_TYPE = 'bpm_model_form_type',
  BPM_TASK_ASSIGN_RULE_TYPE = 'bpm_task_assign_rule_type',
  BPM_PROCESS_INSTANCE_STATUS = 'bpm_process_instance_status',
  BPM_PROCESS_INSTANCE_RESULT = 'bpm_process_instance_result',
  BPM_TASK_ASSIGN_SCRIPT = 'bpm_task_assign_script',
  BPM_OA_LEAVE_TYPE = 'bpm_oa_leave_type',

  // ========== PAY 模块 ==========
  PAY_CHANNEL_CODE = 'pay_channel_code', // 支付渠道编码类型
  PAY_ORDER_STATUS = 'pay_order_status', // 商户支付订单状态
  PAY_REFUND_STATUS = 'pay_refund_status', // 退款订单状态
  PAY_NOTIFY_STATUS = 'pay_notify_status', // 商户支付回调状态
  PAY_NOTIFY_TYPE = 'pay_notify_type', // 商户支付回调状态
  PAY_TRANSFER_STATUS = 'pay_transfer_status', // 转账订单状态
  PAY_TRANSFER_TYPE = 'pay_transfer_type', // 转账订单状态

  // ========== MP 模块 ==========
  MP_AUTO_REPLY_REQUEST_MATCH = 'mp_auto_reply_request_match', // 自动回复请求匹配类型
  MP_MESSAGE_TYPE = 'mp_message_type', // 消息类型

  // ========== MALL - 会员模块 ==========
  MEMBER_POINT_BIZ_TYPE = 'member_point_biz_type', // 积分的业务类型
  MEMBER_EXPERIENCE_BIZ_TYPE = 'member_experience_biz_type', // 会员经验业务类型

  // ========== MALL - 商品模块 ==========
  PRODUCT_UNIT = 'product_unit', // 商品单位
  PRODUCT_SPU_STATUS = 'product_spu_status', //商品状态
  PROMOTION_TYPE_ENUM = 'promotion_type_enum', // 营销类型枚举

  // ========== MALL - 交易模块 ==========
  EXPRESS_CHARGE_MODE = 'trade_delivery_express_charge_mode', //快递的计费方式
  TRADE_AFTER_SALE_STATUS = 'trade_after_sale_status', // 售后 - 状态
  TRADE_AFTER_SALE_WAY = 'trade_after_sale_way', // 售后 - 方式
  TRADE_AFTER_SALE_TYPE = 'trade_after_sale_type', // 售后 - 类型
  TRADE_ORDER_TYPE = 'trade_order_type', // 订单 - 类型
  TRADE_ORDER_STATUS = 'trade_order_status', // 订单 - 状态
  TRADE_ORDER_ITEM_AFTER_SALE_STATUS = 'trade_order_item_after_sale_status', // 订单项 - 售后状态
  TRADE_DELIVERY_TYPE = 'trade_delivery_type', // 配送方式
  BROKERAGE_ENABLED_CONDITION = 'brokerage_enabled_condition', // 分佣模式
  BROKERAGE_BIND_MODE = 'brokerage_bind_mode', // 分销关系绑定模式
  BROKERAGE_BANK_NAME = 'brokerage_bank_name', // 佣金提现银行
  BROKERAGE_WITHDRAW_TYPE = 'brokerage_withdraw_type', // 佣金提现类型
  BROKERAGE_RECORD_BIZ_TYPE = 'brokerage_record_biz_type', // 佣金业务类型
  BROKERAGE_RECORD_STATUS = 'brokerage_record_status', // 佣金状态
  BROKERAGE_WITHDRAW_STATUS = 'brokerage_withdraw_status', // 佣金提现状态

  // ========== MALL - 营销模块 ==========
  PROMOTION_DISCOUNT_TYPE = 'promotion_discount_type', // 优惠类型
  PROMOTION_PRODUCT_SCOPE = 'promotion_product_scope', // 营销的商品范围
  PROMOTION_COUPON_TEMPLATE_VALIDITY_TYPE = 'promotion_coupon_template_validity_type', // 优惠劵模板的有限期类型
  PROMOTION_COUPON_STATUS = 'promotion_coupon_status', // 优惠劵的状态
  PROMOTION_COUPON_TAKE_TYPE = 'promotion_coupon_take_type', // 优惠劵的领取方式
  PROMOTION_ACTIVITY_STATUS = 'promotion_activity_status', // 优惠活动的状态
  PROMOTION_CONDITION_TYPE = 'promotion_condition_type', // 营销的条件类型枚举
  PROMOTION_BARGAIN_RECORD_STATUS = 'promotion_bargain_record_status', // 砍价记录的状态
  PROMOTION_COMBINATION_RECORD_STATUS = 'promotion_combination_record_status', // 拼团记录的状态
  PROMOTION_BANNER_POSITION = 'promotion_banner_position', // banner 定位

  // ========== CRM - 客户管理模块 ==========
  CRM_AUDIT_STATUS = 'crm_audit_status', // CRM 审批状态
  CRM_BIZ_TYPE = 'crm_biz_type', // CRM 业务类型
  CRM_RECEIVABLE_RETURN_TYPE = 'crm_receivable_return_type', // CRM 回款的还款方式
  CRM_CUSTOMER_INDUSTRY = 'crm_customer_industry',
  CRM_CUSTOMER_LEVEL = 'crm_customer_level',
  CRM_CUSTOMER_SOURCE = 'crm_customer_source',
  CRM_PRODUCT_STATUS = 'crm_product_status',
  CRM_PERMISSION_LEVEL = 'crm_permission_level', // CRM 数据权限的级别
  CUSTOM_TYPE = 'custom_type',
  SOURCE_TYPE = 'source_type',
  TRANSPORT_TYPE = 'transport_type',
  PRICE_TYPE = 'price_type',
  CUSTOMER_STAGE = 'customer_stage',
  CRM_CUSTOMER_STATUS = 'audit_type',
  DIFFERENCE_TYPE = 'difference_type',
  ACCOMMODATION_FEE_STANDARD = 'accommodation_fee_standard',

  // ====== OA OA管理模块 ===================
  OA_TRANSPORTATION_TYPE = 'oa_transportation_type',
  LOAN_TRANSFER_STATUS = 'loan_transfer_status',
  LOAN_REPAY_STATUS = 'loan_repay_status',
  LOAN_TYPE = 'loan_type',
  VENDER_LOAN_TYPE = 'vender_loan_type',
  AUXILIARY_TYPE = 'auxiliary_type',
  EXPENSE_TYPE = 'expense_type',
  TRANSPORTATION_TYPE = 'transportation_type',
  TRAVAL_TRANSPORTATION_TYPE = 'traval_transportation_type',
  LOAN_REPAY_TYPE = 'loan_repay_type',
  LEVEL_TYPE = 'level_type',
  ENTERTAIN_EXPENSE_TYPE = 'entertain_expense_type',
  USER_FLAG = 'user_flag',
  EXPENSE_NAME = 'expense_name',
  FEE_TYPE = 'fee_type',
  FEE_SHARE_BUSINESS_TYPE = 'fee_share_business_type',
  ENTWETAIN_TYPE = 'entertain_type',
  CLAIM_OTHER_FEE_TYPE = 'claim_other_fee_type',
  PROJECT_MANAGE_TYPE = 'project_manage_type',
  FEE_BRAND_TYPE = 'fee_brand_type',
  FEE_SHARE_USER_DESC = 'fee_share_user_desc',
  FEE_SHARE_VENDER_DESC = 'fee_share_vender_desc',
  FEE_SHARE_CUST_DESC = 'fee_share_cust_desc',
  REPAY_SOURCE_TYPE = 'repay_source_type',
  PAYMENT_APPLY_TYPE = 'payment_apply_type',

  // ====== 采购模块 ===================
  SCM_STATUS_TYPE = 'scm_status_type',
  VENDER_LEVEL = 'vender_level',
  VENDER_TYPE = 'vender_type',
  PURCHASE_TYPE = 'purchase_type',
  PURCHASE_PLAN_STATUS = 'purchase_plan_status',
  CONVERTED_FLAG = 'converted_flag',
  PURCHASE_CONTRACT_STATUS = 'purchase_contract_status',
  EQUALLY_TYPE = 'equally_type',
  AUXILIARY_PURCHASE_TYPE = 'auxiliary_purchase_type',
  PURCHASE_SOURCE_TYPE = 'purchase_source_type',
  AUXILIARY_CONTRACT_STATUS = 'auxiliary_contract_status',
  INVOICE_STATUS = 'invoice_status',
  REGISTERED_STATUS = 'registered_status',
  VENDER_LOAN_STATUS = 'vender_loan_status',
  ADMINISTRATION_VENDER_TYPE = 'administration_vender_type',
  INVOICE_NOTICE_STATUS = 'invoice_notice_status',
  PURCHASE_CHECK_STATUS = 'purchase_check_status',
  PAYMENT_TYPE = 'payment_type',
  BOX_WITH_COLOR = 'box_with_color',
  PAYMENT_MARK_TYPE = 'payment_mark_type',
  INSPECTION_NODE = 'inspection_node',
  ACCEPTANCE_DAYS = 'acceptance_days',

  // ====== 财务模块 ===================
  BUSINESS_TYPE = 'business_type',
  PAYMENT_STATUS = 'payment_status',
  BUSINESS_SUBJECT_TYPE = 'business_subject_type',
  RECEIPT_BUSINESS_TYPE = 'receipt_business_type',
  RECEIPT_STATUS = 'receipt_status',
  REPAY_STATUS = 'repay_status',
  CHECK_STATUS = 'check_status',
  FMS_PAYMENT_STATUS = 'fms_payment_status',
  RELATED_ORDERS_TYPE = 'related_orders_type',
  CLAIM_STATUS = 'claim_status',
  // ====== 通用字典 ===================
  CURRENCY_TYPE = 'currency_type',
  IS_INT = 'is_int',
  CONFIRM_TYPE = 'confirm_type',
  TAX_TYPE = 'tax_type',
  PRINT_FLAG = 'print_flag',
  ADDRESS_TYPE = 'address_type',
  ENABLE_FLAG = 'enable_flag',
  APPLY_STATUS = 'apply_status',

  //公章图片类型
  OFFICIAL_SEAL_TYPE = 'official_seal_type',

  // ====== 产品模块 ===================
  SINGLE_WIEGHT_UNIT = 'single_weight_unit',
  SPEC_UNIT = 'spec_unit',
  MEASURE_UNIT = 'measure_unit',
  SKU_TYPE = 'sku_type',
  SOURCE_FLAG = 'source_flag',
  ONSHELF_FLAG = 'onshelf_flag',
  PACKAGE_TYPE = 'package_type',
  // ====== 仓库模块 ===================
  NOTICE_STATUS = 'notice_status',
  STOCK_BILL_STATUS = 'stock_bill_status',
  STOCK_SOURCE_STATUS = 'stock_source_type',
  IN_OUT_TYPE = 'in_out_type',
  STOCK_TAKE = 'stock_take',
  VENDER_WMS_TYPE = 'vender_wms_type',
  IN_OUT_STATUS = 'in_out_status',
  ABNORMAL_STATUS = 'abnormal_status',
  BILL_STATUS = 'bill_status',
  STOCK_LOCK_SOURCE_TYPE = 'stock_lock_source_type',
  STOCK_METHOD = 'stock_method',
  OUT_STOCK_SOURCE_TYPE = 'out_stock_source_type',

  // ====== 外销合同 ===================
  CALCULATION_TYPE = 'calculation_type',
  COMMISSION_TYPE = 'commission_type',
  SALE_TYPE = 'sale_type',
  SALE_CONTRACT_STATUS = 'sale_contract_status',
  DOME_SALE_CONTRACT_STATUS = 'dome_sale_contract_status',
  EXPORT_SALE_CONTRACT_PRINT_FTJD_TYPE = 'export_sale_contract_ftjd_print_type',
  SALE_ITEM_BILL_STATUS = 'sale_item_bill_status',

  // ====== 收款计划 ===================
  EXECUTE_STATUS = 'execute_status',
  COLLECTION_PLAN_STEP = 'collection_plan_step',
  PAY_METHOD = 'pay_method',

  // ====== 付款计划 ===================
  PAYMENT_PLAN_STEP = 'payment_plan_step',
  // ====== 出运 ===================
  SHIPPING_STATUS = 'shipping_status',
  SHIPPING_PLAN_STATUS = 'shipping_plan_status',
  COMMODITY_INSPECTION_TYPE = 'commodity_inspection_type',
  TO_SETTLEMENTFORM_TYPE = 'to_settlementForm_type',
  HS_MEASURE_UNIT = 'hs_measure_unit',
  APPLY_FEE_STATUS = 'apply_fee_status',
  FORWARDER_FEE_TYPE = 'forwarder_fee_type',
  SHIPPED_ADDRESS = 'shipped_address',
  SHIPMENT_TYPE = 'shipment_type',

  // ====== qms  质检 ===================
  INSPECTION_TYPE = 'inspection_type',
  QUALITY_INSPECTION_STATUS = 'quality_inspection_status',
  INSPECTION_ALLOCATION_TYPE = 'inspection_allocation_type',
  // INSPECTION_STATUS = 'inspection_status',
  INSPECT_RESULT_TYPE = 'inspect_result_type',
  QMS_HANDLE_STATE = 'qms_handle_state',

  // ====== dtms  设计 ===================
  DESIGN_TYPE = 'design_type',
  DESIGN_STATUS = 'design_status',
  APPROVED_TYPE = 'approved_type',
  CONTRACT_TYPE = 'contract_type',

  // ====== ems  寄件 ===================
  SEND_REGION = 'send_region',
  SEND_PAY_TYPE = 'send_pay_type',
  SEND_PRODUCT_TYPE = 'send_product_type',
  SEND_STATUS = 'send_status',
  GOODS_SOURCE = 'goods_source',
  INSPECTION_STATUS = 'inspection_status',
  FEE_SHARE_TYPE = 'fee_share_type',
  COMPANY_OPERATE_TYPE = 'company_operate_type',
  MARKET_EXPANSION_TYPE = 'market_expansion_type',
  ORDER_TYPE = 'order_type',
  NO_ORDER_BUS_TYPE = 'no_order_bus_type',
  EMS_RECEIVE_TYPE = 'ems_receive_type',

  // ====== 加工 ===================
  MMS_MANUFACTURE_STATUS = 'mms_manufacture_status',
  EXMS_EXHIBITION_THEME = 'exms_exhibition_theme',
  EXMS_STALL_THEME = 'exms_stall_theme',
  EXMS_EXHIBITION_STATUS = 'exms_exhibition_status',
  PJMS_PROJECT_STATUS = 'pjms_project_status',
  PJMS_DEVELOP_TYPE = 'pjms_develop_type',
  ALLOCATE_CONDITION_TYPE = 'allocate_condition_type',
  ALLOCATE_TYPE = 'allocate_type',

  // ====== 表单变更管理 ===================
  CHANGE_LEVEL = 'change_level',
  EFFECT_RANGE = 'effect_range',
  // ====== 首页 ===================
  REIMB_TYPE = 'reimb_type',
  INVOICE_TYPE = 'invoice_type',

  //报价单状态
  QUOTATION_STATUS = 'quotation_status',

  // ====== 变更相关 ===================
  CHANGE_TYPE = 'change_type',
  CONFIRM_SOURCE_TYPE = 'confirm_source_type',
  BRAND_TYPE = 'brand_type',

  // ====== 认领相关 ===================
  PROGRESS_TYPE = 'progress_type',

  //外箱单位
  UNIT_PER_OUTERBOX_TYPE = 'unit_per_outerbox_type',

  //出差类型
  TRAVEL_TYPE = 'travel_type'
}
