/**
 * Created by 外贸源码
 *
 * 枚举类
 */

// ========== COMMON 模块 ==========
// 全局通用状态枚举
export const CommonStatusEnum = {
  ENABLE: 0, // 开启
  DISABLE: 1 // 禁用
}

// 全局用户类型枚举
export const UserTypeEnum = {
  MEMBER: 1, // 会员
  ADMIN: 2 // 管理员
}

// ========== SYSTEM 模块 ==========
/**
 * 菜单的类型枚举
 */
export const SystemMenuTypeEnum = {
  DIR: 1, // 目录
  MENU: 2, // 菜单
  BUTTON: 3 // 按钮
}

/**
 * 角色的类型枚举
 */
export const SystemRoleTypeEnum = {
  SYSTEM: 1, // 内置角色
  CUSTOM: 2 // 自定义角色
}

/**
 * 数据权限的范围枚举
 */
export const SystemDataScopeEnum = {
  ALL: 1, // 全部数据权限
  DEPT_CUSTOM: 2, // 指定部门数据权限
  DEPT_ONLY: 3, // 部门数据权限
  DEPT_AND_CHILD: 4, // 部门及以下数据权限
  DEPT_SELF: 5 // 仅本人数据权限
}

/**
 * 用户的社交平台的类型枚举
 */
export const SystemUserSocialTypeEnum = {
  DINGTALK: {
    title: '钉钉',
    type: 20,
    source: 'dingtalk',
    img: 'https://s1.ax1x.com/2022/05/22/OzMDRs.png'
  },
  WECHAT_ENTERPRISE: {
    title: '企业微信',
    type: 30,
    source: 'wechat_enterprise',
    img: 'https://s1.ax1x.com/2022/05/22/OzMrzn.png'
  }
}

// ========== INFRA 模块 ==========
/**
 * 代码生成模板类型
 */
export const InfraCodegenTemplateTypeEnum = {
  CRUD: 1, // 基础 CRUD
  TREE: 2, // 树形 CRUD
  SUB: 3 // 主子表 CRUD
}

/**
 * 任务状态的枚举
 */
export const InfraJobStatusEnum = {
  INIT: 0, // 初始化中
  NORMAL: 1, // 运行中
  STOP: 2 // 暂停运行
}

/**
 * API 异常数据的处理状态
 */
export const InfraApiErrorLogProcessStatusEnum = {
  INIT: 0, // 未处理
  DONE: 1, // 已处理
  IGNORE: 2 // 已忽略
}

// ========== PAY 模块 ==========
/**
 * 支付渠道枚举
 */
export const PayChannelEnum = {
  WX_PUB: {
    code: 'wx_pub',
    name: '微信 JSAPI 支付'
  },
  WX_LITE: {
    code: 'wx_lite',
    name: '微信小程序支付'
  },
  WX_APP: {
    code: 'wx_app',
    name: '微信 APP 支付'
  },
  WX_BAR: {
    code: 'wx_bar',
    name: '微信条码支付'
  },
  ALIPAY_PC: {
    code: 'alipay_pc',
    name: '支付宝 PC 网站支付'
  },
  ALIPAY_WAP: {
    code: 'alipay_wap',
    name: '支付宝 WAP 网站支付'
  },
  ALIPAY_APP: {
    code: 'alipay_app',
    name: '支付宝 APP 支付'
  },
  ALIPAY_QR: {
    code: 'alipay_qr',
    name: '支付宝扫码支付'
  },
  ALIPAY_BAR: {
    code: 'alipay_bar',
    name: '支付宝条码支付'
  },
  MOCK: {
    code: 'mock',
    name: '模拟支付'
  }
}

/**
 * 支付的展示模式每局
 */
export const PayDisplayModeEnum = {
  URL: {
    mode: 'url'
  },
  IFRAME: {
    mode: 'iframe'
  },
  FORM: {
    mode: 'form'
  },
  QR_CODE: {
    mode: 'qr_code'
  },
  APP: {
    mode: 'app'
  }
}

/**
 * 支付类型枚举
 */
export const PayType = {
  WECHAT: 'WECHAT',
  ALIPAY: 'ALIPAY',
  MOCK: 'MOCK'
}

/**
 * 支付订单状态枚举
 */
export const PayOrderStatusEnum = {
  WAITING: {
    status: 0,
    name: '未支付'
  },
  SUCCESS: {
    status: 10,
    name: '已支付'
  },
  CLOSED: {
    status: 20,
    name: '未支付'
  }
}

// ========== MALL - 商品模块 ==========
/**
 * 商品 SPU 状态
 */
export const ProductSpuStatusEnum = {
  RECYCLE: {
    status: -1,
    name: '回收站'
  },
  DISABLE: {
    status: 0,
    name: '下架'
  },
  ENABLE: {
    status: 1,
    name: '上架'
  }
}

// ========== MALL - 营销模块 ==========
/**
 * 优惠劵模板的有限期类型的枚举
 */
export const CouponTemplateValidityTypeEnum = {
  DATE: {
    type: 1,
    name: '固定日期可用'
  },
  TERM: {
    type: 2,
    name: '领取之后可用'
  }
}

/**
 * 优惠劵模板的领取方式的枚举
 */
export const CouponTemplateTakeTypeEnum = {
  USER: {
    type: 1,
    name: '直接领取'
  },
  ADMIN: {
    type: 2,
    name: '指定发放'
  },
  REGISTER: {
    type: 3,
    name: '新人券'
  }
}

/**
 * 营销的商品范围枚举
 */
export const PromotionProductScopeEnum = {
  ALL: {
    scope: 1,
    name: '通用劵'
  },
  SPU: {
    scope: 2,
    name: '商品劵'
  },
  CATEGORY: {
    scope: 3,
    name: '品类劵'
  }
}

/**
 * 营销的条件类型枚举
 */
export const PromotionConditionTypeEnum = {
  PRICE: {
    type: 10,
    name: '满 N 元'
  },
  COUNT: {
    type: 20,
    name: '满 N 件'
  }
}

/**
 * 优惠类型枚举
 */
export const PromotionDiscountTypeEnum = {
  PRICE: {
    type: 1,
    name: '满减'
  },
  PERCENT: {
    type: 2,
    name: '折扣'
  }
}

// ========== MALL - 交易模块 ==========
/**
 * 分销关系绑定模式枚举
 */
export const BrokerageBindModeEnum = {
  ANYTIME: {
    mode: 1,
    name: '首次绑定'
  },
  REGISTER: {
    mode: 2,
    name: '注册绑定'
  },
  OVERRIDE: {
    mode: 3,
    name: '覆盖绑定'
  }
}
/**
 * 分佣模式枚举
 */
export const BrokerageEnabledConditionEnum = {
  ALL: {
    condition: 1,
    name: '人人分销'
  },
  ADMIN: {
    condition: 2,
    name: '指定分销'
  }
}
/**
 * 佣金记录业务类型枚举
 */
export const BrokerageRecordBizTypeEnum = {
  ORDER: {
    type: 1,
    name: '获得推广佣金'
  },
  WITHDRAW: {
    type: 2,
    name: '提现申请'
  }
}

export const AuditStatusEnum = {
  EDITING: {
    status: 1,
    name: '编辑中'
  },
  PENDING: {
    status: 2,
    name: '审核中'
  },
  APPROVE: {
    status: 3,
    name: '审核通过'
  },
  REJECT: {
    status: 4,
    name: '不通过'
  }
}

export const SaleContractStatusEnum = {
  INSUBMITTED: {
    status: 1,
    name: '待提交'
  },
  INAPPROVE: {
    status: 2,
    name: '待审核'
  },
  INSINGBACK: {
    status: 3,
    name: '待回签'
  },
  REJECTED: {
    status: 4,
    name: '已驳回'
  },
  INPURCHASE: {
    status: 5,
    name: '待采购'
  },
  INSHIPPING: {
    status: 6,
    name: '待出运'
  },
  COMPLETE: {
    status: 7,
    name: '已完成'
  },
  CANCEL: {
    status: 8,
    name: '已作废'
  },
  CONFIRM: {
    status: 9,
    name: '上游变更'
  }
}

export const QuotationStatusEnum = {
  INSUBMITTED: {
    status: 1,
    name: '待提交'
  },
  INAPPROVE: {
    status: 2,
    name: '待审核'
  },
  APPROVED: {
    status: 3,
    name: '已审核'
  },
  INPURCHASE: {
    status: 4,
    name: '已驳回'
  },
  FINISH: {
    status: 5,
    name: '已作废'
  }
}

export const BpmProcessInstanceResultEnum = {
  UNSUBMITTED: {
    status: 0,
    name: '未提交'
  },
  PROCESS: {
    status: 1,
    name: '处理中'
  },
  APPROVE: {
    status: 2,
    name: '通过'
  },
  REJECT: {
    status: 3,
    name: '不通过'
  },
  CANCEL: {
    status: 4,
    name: '已取消'
  },
  CLOSE: {
    status: 10,
    name: '已作废'
  }
}

export const PurchaseRegistrationStatusEnum = {
  PROCESS: {
    status: 1,
    name: '未审批'
  },
  APPROVE: {
    status: 2,
    name: '已审批'
  },
  REJECT: {
    status: 3,
    name: '已作废'
  }
}

/**
 * 支付订单状态枚举
 */
export const PaymentStatusEnum = {
  WAITING: {
    status: 0,
    name: '未支付'
  },
  SUCCESS: {
    status: 1,
    name: '已支付'
  }
}

/**
 * 佣金提现状态枚举
 */
export const BrokerageWithdrawStatusEnum = {
  AUDITING: {
    status: 0,
    name: '审核中'
  },
  AUDIT_SUCCESS: {
    status: 10,
    name: '审核通过'
  },
  AUDIT_FAIL: {
    status: 20,
    name: '审核不通过'
  },
  WITHDRAW_SUCCESS: {
    status: 11,
    name: '提现成功'
  },
  WITHDRAW_FAIL: {
    status: 21,
    name: '提现失败'
  }
}
/**
 * 佣金提现类型枚举
 */
export const BrokerageWithdrawTypeEnum = {
  WALLET: {
    type: 1,
    name: '钱包'
  },
  BANK: {
    type: 2,
    name: '银行卡'
  },
  WECHAT: {
    type: 3,
    name: '微信'
  },
  ALIPAY: {
    type: 4,
    name: '支付宝'
  }
}

/**
 * 配送方式枚举
 */
export const DeliveryTypeEnum = {
  EXPRESS: {
    type: 1,
    name: '快递发货'
  },
  PICK_UP: {
    type: 2,
    name: '到店自提'
  }
}
/**
 * 交易订单 - 状态
 */
export const TradeOrderStatusEnum = {
  UNPAID: {
    status: 0,
    name: '待支付'
  },
  UNDELIVERED: {
    status: 10,
    name: '待发货'
  },
  DELIVERED: {
    status: 20,
    name: '已发货'
  },
  COMPLETED: {
    status: 30,
    name: '已完成'
  },
  CANCELED: {
    status: 40,
    name: '已取消'
  }
}

/**
 * 采购合同-合同状态
 */
export const PurchaseContractStatusEnum = {
  WAITING_FOR_CONFIRMATION: {
    status: 0,
    name: '上游变更'
  },
  READY_TO_SUBMIT: {
    status: 1,
    name: '待提交'
  },
  AWAITING_APPROVAL: {
    status: 2,
    name: '待审批'
  },
  REJECTED: {
    status: 3,
    name: '已驳回'
  },
  AWAITING_ORDER: {
    status: 4,
    name: '待下单'
  },
  EXPECTING_DELIVERY: {
    status: 5,
    name: '待出运'
  },
  FINISHED: {
    status: 6,
    name: '已完成'
  },
  CASE_SETTLED: {
    status: 7,
    name: '已作废'
  }
}
/**
 * 采购计划订单 - 状态
 */
export const PurchasePlanStatusEnum = {
  UNSUBMIT: {
    status: 1,
    name: '未提交'
  },
  PROCESS: {
    status: 2,
    name: '待审批'
  },
  UNPURCHASE: {
    status: 3,
    name: '待采购'
  },
  REJECTED: {
    status: 4,
    name: '已驳回'
  },
  COMPLETED: {
    status: 5,
    name: '已完成'
  },
  FINISH: {
    status: 6,
    name: '已作废'
  }
}
/**
 * 口岸状态 - 状态
 */
export const PortStatusStatusEnum = {
  NORMAL: {
    status: 1,
    name: '正常'
  },
  SHUTDOWN: {
    status: 2,
    name: '停运'
  },
  INVALID: {
    status: 3,
    name: '无效'
  }
}
/**
 * sku产品类型状态 - 状态
 */
export const SkuTypeEnum = {
  COMMON: {
    status: 1,
    name: '普通产品'
  },
  COMPOSITE: {
    status: 2,
    name: '组合产品'
  },
  ACCESSORY: {
    status: 3,
    name: '配件'
  },
  AUXILIARY: {
    status: 4,
    name: '辅料'
  }
}
/**
 * 打印模板类型
 */
export const ReportTypeEnum = {
  BASIC: {
    status: 1,
    name: '普通模板'
  },
  COMPANY: {
    status: 2,
    name: '账套模板'
  },
  DIY: {
    status: 3,
    name: '外部模板'
  },
  SPECIFIC: {
    status: 4,
    name: '可选模板'
  }
}
/**
 * 辅料采购类型
 */
export const AuxiliaryPurchaseType = {
  ORDER: {
    type: 1,
    name: '订单相关'
  },
  SKU: {
    type: 2,
    name: '产品相关'
  }
}
/**
 * 采购类型 1-库存采购 2-标准采购 3-配件辅料采购 4-样品采购
 */
export const PurchaseTypeEnum = {
  STOCK: {
    type: 1,
    name: '库存采购'
  },
  STANDARD: {
    type: 2,
    name: '标准采购'
  },
  AUXILIARY: {
    type: 3,
    name: '配件辅料采购'
  },
  SAMPLE: {
    type: 4,
    name: '样品采购'
  }
}
/**
 * 辅料采购类型
 */
export const PurchaseSourceTypeEnum = {
  MANUALADD: {
    type: 1,
    name: '手工新建'
  },
  SALECONTRACT: {
    type: 2,
    name: '外销合同'
  }
}
/**
 * 质检-验货单 - 状态
 */
export const QualityInspectStatusEnum = {
  PROCESS: {
    status: 1,
    name: '待审批'
  },
  WAITING_FOR_CONFIRMATION: {
    status: 2,
    name: '待排验'
  },
  WAITING_FOR_INSPECTION: {
    status: 3,
    name: '待验货'
  },
  WAITING_FAILED: {
    status: 4,
    name: '验货不通过'
  },
  WAITING_PASS: {
    status: 5,
    name: '验货通过'
  },
  REJECTED: {
    status: 7,
    name: '已驳回'
  },
  COMPLETED: {
    status: 6,
    name: '部分通过'
  },
  FINISH: {
    status: 8,
    name: '已作废'
  }
}
/**
 * 设计任务-设计任务单 - 状态
 */
export const DesignTaskStatusEnum = {
  WAITING_SUBMIT: {
    status: 1,
    name: '待提交'
  },
  WAITINGG_AUDIT: {
    status: 2,
    name: '待审批'
  },
  WAITING_COMPLETE: {
    status: 3,
    name: '待完成'
  },
  WAITING_EVALUATE: {
    status: 4,
    name: '待评价'
  },
  COMPLETE: {
    status: 5,
    name: '已完成'
  },
  CLOSE_CASE: {
    status: 6,
    name: '已作废'
  },
  REJECTED: {
    status: 7,
    name: '已驳回'
  }
}

/**
 * 寄件 寄件列表 - 状态
 */
export const SendStatusEnum = {
  WAITING_SUBMIT: {
    status: 1,
    name: '待提交'
  },
  WAITINGG_AUDIT: {
    status: 2,
    name: '待审批'
  },
  REJECTED: {
    status: 3,
    name: '已驳回'
  },
  WAITINGG_SEND: {
    status: 4,
    name: '待寄出'
  },
  SENT_OUT: {
    status: 5,
    name: '已寄出'
  },
  PENDING_PAYMENT: {
    status: 6,
    name: '待请款'
  },
  COMPLETE: {
    status: 7,
    name: '已完成'
  },
  CLOSE: {
    status: 8,
    name: '已作废'
  }
}

/**
 * 财务付款状态
 * 0：未提交1：处理中2：通过3：不通过4：已取消5：驳回6：委派7：待后加签任务完成8：待前加签任务完成9：待前置任务完成10：已计划11：已支付
 */
export const PaymentAuditStatus = {
  UNSUBMITTED: {
    status: 0,
    name: '未提交'
  },
  PROCESS: {
    status: 1,
    name: '处理中'
  },
  APPROVE: {
    status: 2,
    name: '通过'
  },
  REJECT: {
    status: 3,
    name: '不通过'
  },
  CANCEL: {
    status: 4,
    name: '已取消'
  },
  REJECTED: {
    status: 5,
    name: '驳回'
  },
  DELEGATE: {
    status: 6,
    name: '委派'
  },
  WAITING_AFTER_SIGN_TASK: {
    status: 7,
    name: '待后加签任务完成'
  },
  WAITING_BEFORE_SIGN_TASK: {
    status: 8,
    name: '待前加签任务完成'
  },
  WAITING_PRE_TASK: {
    status: 9,
    name: '待前置任务完成'
  },
  PLANNED: {
    status: 10,
    name: '已计划'
  },
  PAID: {
    status: 11,
    name: '已支付'
  },
  CLOSE: {
    status: 12,
    name: '作废'
  },
  PART_PLANNED: {
    status: 13,
    name: '部分计划'
  },
  PART_PAID: {
    status: 14,
    name: '部分支付'
  }
}
/**
 * 费用类型
 * 1：差旅费报销2：招待费报销3：一般费用报销4：寄件5：对公付款
 */
export const CostTypes = {
  TRAVELREIMB: {
    status: 1,
    name: '差旅费报销'
  },
  ENTERTAINMENT: {
    status: 3,
    name: '招待费报销'
  },
  GENERAL: {
    status: 2,
    name: '通用费用报销'
  },
  SEND: {
    status: 5,
    name: '寄件'
  },
  CORPORATE: {
    status: 4,
    name: '对公付款'
  },
  FORWARDER: {
    status: 6,
    name: '船代费用'
  },
  OTHER: {
    status: 11,
    name: '其他费用报销'
  }
}
/**
 * 出运计划状态枚举
 * 1：待处理2：已完成3：已作废
 */
export const ShipmentPlanStatusEnum = {
  WAITING_SUBMIT: {
    status: 0,
    name: '待提交'
  },
  WAITING_PROCESS: {
    status: 1,
    name: '待转明细'
  },
  COMPLETED: {
    status: 2,
    name: '已转明细'
  },
  FINISH: {
    status: 3,
    name: '已作废'
  }
}
/**
 * 出运明细状态枚举
 * 1：待处理2：待出运3：分批出运4：已出运5：已完成6：已作废
 */
export const ShipmentDetailStatusEnum = {
  WAITING_PROCESS: {
    status: 1,
    name: '待处理'
  },
  WAITING_SHIPMENT: {
    status: 2,
    name: '待出运'
  },
  PARTIAL_SHIPMENT: {
    status: 3,
    name: '分批出运'
  },
  SHIPPED: {
    status: 4,
    name: '已出运'
  },
  COMPLETED: {
    status: 5,
    name: '已完成'
  },
  FINISH: {
    status: 6,
    name: '已作废'
  }
}
/**
 * 入库状态枚举
 * 1：未转2：已转3：作废4：部分转5：转单中
 */
export const WarehouseStatusEnum = {
  UNTRANSFERRED: {
    status: 1,
    name: '未转'
  },
  TRANSFERRED: {
    status: 2,
    name: '已转'
  },
  INVALID: {
    status: 3,
    name: '作废'
  },
  PARTIAL_TRANSFERRED: {
    status: 4,
    name: '部分转'
  },
  TRANSFERRED_IN_PROGRESS: {
    status: 5,
    name: '转单中'
  }
}
/**
 * 商检负责方枚举
 * 1：供应商2：自主商检
 */
export const InspectResponsiblePartyEnum = {
  SUPPLIER: {
    status: 1,
    name: '供应商'
  },
  SELF: {
    status: 2,
    name: '自主商检'
  }
}
/**
 * 转入库通知单状态枚举
 * 1：未转2：部分转3：已转
 */
export const ConvertNoticeFlagEnum = {
  UNTRANSFERRED: {
    status: 1,
    name: '未转'
  },
  PARTIAL_TRANSFERRED: {
    status: 2,
    name: '部分转'
  },
  TRANSFERRED: {
    status: 3,
    name: '已转'
  }
}
/**
 * 转开票通知单状态枚举
 * 1：已转2：部分转3：未转
 */
export const ConvertInvoiceFlagEnum = {
  TRANSFERRED: {
    status: 1,
    name: '已转'
  },
  PARTIAL_TRANSFERRED: {
    status: 2,
    name: '部分转'
  },
  UNTRANSFERRED: {
    status: 3,
    name: '未转'
  }
}
/**
 * 关联单据类型枚举
 *1、销售合同、2：销售合同变更、3：采购计划、4：采购合同、5：采购合同变更、6：出运计划、7：出运明细、8：出运明细变更、9：供应商变更、10：客户变更、11：拉柜通知单、12：报关单、13：结汇单、14：商检单、15：入库单、16：验货单、17：付款单、18：退货单、19：辅料采购合同
 */
export const RelatedOrdersTypeEnum = {
  SALES_CONTRACT: {
    status: 1,
    name: '销售合同'
  },
  SALES_CONTRACT_CHANGE: {
    status: 2,
    name: '销售合同变更'
  },
  PURCHASE_PLAN: {
    status: 3,
    name: '采购计划'
  },
  PURCHASE_CONTRACT: {
    status: 4,
    name: '采购合同'
  },
  PURCHASE_CONTRACT_CHANGE: {
    status: 5,
    name: '采购合同变更'
  },
  SHIPMENT_PLAN: {
    status: 6,
    name: '出运计划'
  },
  SHIPMENT_DETAIL: {
    status: 7,
    name: '出运明细'
  },
  SHIPMENT_DETAIL_CHANGE: {
    status: 8,
    name: '出运明细变更'
  },
  SUPPLIER_CHANGE: {
    status: 9,
    name: '供应商变更'
  },
  CUSTOMER_CHANGE: {
    status: 10,
    name: '客户变更'
  },
  PULL_CABINET_NOTICE: {
    status: 11,
    name: '拉柜通知单'
  },
  CUSTOMS_DECLARATION: {
    status: 12,
    name: '报关单'
  },
  SETTLEMENT: {
    status: 13,
    name: '结汇单'
  },
  INSPECTION: {
    status: 14,
    name: '商检单'
  },
  STOCK_IN: {
    status: 15,
    name: '入库单'
  },
  INSPECTION_REPORT: {
    status: 16,
    name: '验货单'
  },
  PAYMENT: {
    status: 17,
    name: '付款单'
  },
  RETURN: {
    status: 18,
    name: '退货单'
  },
  ACCESSORIES_PURCHASE_CONTRACT: {
    status: 19,
    name: '辅料采购合同'
  }
}
