import request from '@/config/axios'

export interface purchaseContractVO {
  id: number
  ver: number
  code: string
  auditStatus: number
  contractStatus: number
  totalAmount: JSON
  totalQuantity: number
  printFlag: number
  printTimes: number
  prepayStatus: number
  prepayAmount: number
  payStatus: number
  payedAmount: JSON
  invoicedStatus: number
  invoicedAmount: JSON
  trackUserId: number
  trackUserName: string
  purchaseUserId: number
  purchaseUserName: string
  purchaseUserDeptId: number
  purchaseUserDeptName: string
  custId: number
  custCdoe: string
  venderId: number
  venderCode: string
  stockId: number
  stockCode: string
  stckName: string
  purchasePlanId: number
  purchasePlanCode: string
  smsContractId: number
  smsContractCode: string
  remark: string
  annex: JSON
  companyId: number
  purchaseTime: Date
  venderPaymentId: number
  syncQuoteFlag: number
  freeFlag: number
  PortId: number
  freight: JSON
  OtherCost: JSON
  deliveryTime: Date
  PlannedArrivalTime: Date
  fileList: []
  auditInfo: {
    taskId: string
    createUser: string
    auditUser: string
    auditStatus: number
  }
  submitFlag: number
}

// 查询采购合同信息分页
export const getPurchaseContractPage = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/page`, params })
}

// 查询采购合同信息分页
export const getPurchaseContractBoardPage = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/page-board`, params })
}

// 查询采购合同信息详情
export const getPurchaseContract = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/detail`, params })
}

//审核模块的获取详情
export const getAuditpurchaseContract = async (params) => {
  return await request.get({ url: '/scm/purchase-contract/audit-detail', params })
}

// 新增采购合同信息
export const createPurchaseContract = async (data: purchaseContractVO) => {
  return await request.post({ url: `/scm/purchase-contract/create`, data })
}

// 修改采购合同信息
export const updatePurchaseContract = async (data: purchaseContractVO) => {
  return await request.put({ url: `/scm/purchase-contract/update`, data })
}

// 删除采购合同信息
export const deletePurchaseContract = async (id: number) => {
  return await request.delete({ url: `/scm/purchase-contract/delete?id=` + id })
}

// 导出采购合同信息 Excel
export const exportPurchaseContract = async (params) => {
  return await request.download({ url: `/scm/purchase-contract/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//应付采购合同
export const getPurchaseContractSimpleList = async (params?) => {
  return await request.get({ url: `/scm/purchase-contract/get-simple-list`, params })
}

//采购合同提交
export const submitPurchaseContract = async (id) => {
  return await request.put({ url: `/scm/purchase-contract/submit?paymentId=${id}` })
}
//获取产品精简列表
export const getSkuSimpleList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-sku`, params })
}

export const toFormalPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/convert`, data })
}
//获取目的口岸分页
export const getPortList = async (params) => {
  return await request.get({ url: `/system/port/get-simple-list`, params })
}
//作废
export const batchFinishPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-finish?contractIdList=${data}` })
}
//完成单据
export const orderDonePurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/order-done?id=${data}` })
}
//整单完成
export const batchDonePurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-done?contractIdList=${data}` })
}
//生产完成
export const produceDonePurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/complete-order`, data })
}
//反作废
export const batchRollBackFinishPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/purchase-contract/batch-rollback-finish?contractIdList=${data}`
  })
}
//下单
export const batchOrderPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-order?contractIdList=${data}` })
}
export const placeOrderPurchaseContract = async (id) => {
  return await request.put({
    url: `/scm/purchase-contract/place-order?id=${id}`
  })
}
//退换货
export const exchangePurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-exchange?contractIdList=${data}` })
}
//请款
export const payPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-pay?contractIdList=${data}` })
}
//验货通知单
export const checkPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-check?contractIdList=${data}` })
}
//入库通知单
export const warehousingPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/purchase-contract/batch-warehousing?contractIdList=${data}`
  })
}
//变更
export const changePurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/batch-change?contractIdList=${data}` })
}
//回签
export const signBackPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/sign-back`, data: data })
}
//获取仓库精简列表
export const getStockList = async () => {
  return await request.get({ url: `/wms/warehouse/get-simple-list` })
}
//采购合同转入库通知单
export const contractToStock = async (data) => {
  return await request.post({ url: `/scm/purchase-contract/to-notice`, data })
}
//打印
export const print = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/print`, params })
}
// 验货通知单
export const createQualityInspection = async (data) => {
  return await request.post({ url: '/qms/quality-inspection/create', data })
}

//获取付款计划
export const getPaymentPlan = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/get-payment-list`, params })
}
//申请付款单中间页
export const getPayment = async (params) => {
  return await request.get({ url: `/scm/payment-apply/message`, params })
}

// 获取确认来源列表
export const getPurchaseConfirmSource = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/confirm-source`, params })
}

// 确认
export const changeConfirm = async (id) => {
  return await request.put({ url: `/scm/purchase-contract/confirm?id=${id}` })
}

export const updateDesign = async (data) => {
  return await request.put({ url: `/scm/purchase-contract/update-design`, data })
}
//创建开票通知
export const createInvoice = async (data) => {
  return await request.post({ url: `/scm/invoicing-notices/create`, data })
}
// 创建开票通知单中间页
export const toInvoiceNotice = async (data) => {
  return await request.post({ url: `/scm/purchase-contract/to-invoice-notice`, data })
}
//批量转入库通知单中间页
export const batchToNoticeMid = async (ids) => {
  return await request.get({ url: `/scm/purchase-contract/batch-to-notice-mid?ids=${ids}` })
}
//批量转入库通知单
export const batchToNotice = async (data) => {
  return await request.post({ url: `/scm/purchase-contract/batch-to-notice`, data })
}

// 查询辅料信息
export const auxiliaryList = async (params) => {
  return await request.get({ url: `/scm/purchase-contract/batch-to-auxiliary-mid`, params })
}

export const toAuxiliary = async (data) => {
  return await request.post({ url: `/scm/purchase-contract/batch-to-auxiliary`, data })
}

// 包材分摊
export const getAuxiliaryAllocation = async (data) => {
  return await request.post({
    url: `/scm/auxiliary-purchase-contract/get-auxiliary-allocation`,
    data
  })
}
export const updateAuxiliaryAllocation = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/update-auxiliary-allocation`,
    data
  })
}
export const deleteAuxiliaryAllocation = async (params) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/delete-auxiliary-allocation`,
    params
  })
}
// 通过审核
export const contractApprove = async (data) => {
  return await request.put({ url: '/scm/purchase-contract/approve', data: data })
}
// 审核不通过
export const contractReject = async (data) => {
  return await request.put({ url: '/scm/purchase-contract/reject', data: data })
}

//采购合同变更影响
export const getContractChangeEffect = async (data) => {
  return await request.post({ url: `/scm/purchase-contract/change-effect`, data })
}

export const exportPurchaseContractDetail = async (params) => {
  return await request.download({
    url: `/scm/purchase-contract/export-excel-detail`,
    params,
    neverLoading: true
  })
}

export const exportPurchaseContractDetailWord = async (params) => {
  return await request.download({
    url: `/scm/purchase-contract/export-word`,
    params,
    neverLoading: true
  })
}

export const toInvoiceNoticeDetail = async (id) => {
  return await request.get({
    url: `/scm/purchase-contract/to-invoice-notice-mid?id=${id}`
  })
}

// 查询全部付款计划
export const paymentPlanList = async (code) => {
  return await request.get({
    url: `/scm/purchase-contract/get-payment-plan-list?code=${code}`
  })
}

//获取报价相关供应商列表
export const venderQuoteList = async (code) => {
  return await request.get({
    url: `/scm/quote-item/get-same-vender-quote-list?skuCodeList=${code}`
  })
}

// 修改采购单号
export const updateCode = async (params) => {
  return await request.put({
    url: `/scm/purchase-contract/update-code`,
    params
  })
}

// 修改付款计划
export const updatePaymentPlan = async (data) => {
  return await request.put({
    url: `/scm/purchase-contract/update-payment-plan`,
    data
  })
}
