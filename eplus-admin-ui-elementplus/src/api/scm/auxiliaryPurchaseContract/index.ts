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
  return await request.get({ url: `/scm/auxiliary-purchase-contract/page`, params })
}

// 查询采购合同信息分页
export const getPurchaseContractBoardPage = async (params) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/page-board`, params })
}

// 查询采购合同信息详情
export const getPurchaseContract = async (params) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/detail`, params })
}

//审核模块的获取详情
export const getAuditpurchaseContract = async (params) => {
  return await request.get({ url: '/scm/auxiliary-purchase-contract/audit-detail', params })
}

// 新增采购合同信息
export const createPurchaseContract = async (data: purchaseContractVO) => {
  return await request.post({ url: `/scm/auxiliary-purchase-contract/create`, data })
}

// 修改采购合同信息
export const updatePurchaseContract = async (data: purchaseContractVO) => {
  return await request.put({ url: `/scm/auxiliary-purchase-contract/update`, data })
}

// 删除采购合同信息
export const deletePurchaseContract = async (id: number) => {
  return await request.delete({ url: `/scm/auxiliary-purchase-contract/delete?id=` + id })
}

// 导出采购合同信息 Excel
export const exportPurchaseContract = async (params) => {
  return await request.download({ url: `/scm/auxiliary-purchase-contract/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//应付采购合同
export const getPayablePurchaseContract = async (params?) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/get-simple-list`, params })
}

//采购合同提交
export const submitPurchaseContract = async (id) => {
  return await request.put({ url: `/scm/auxiliary-purchase-contract/submit?paymentId=${id}` })
}
//获取产品精简列表
export const getSkuSimpleList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-sku`, params })
}

export const toFormalPurchaseContract = async (data) => {
  return await request.put({ url: `/scm/auxiliary-purchase-contract/convert`, data })
}
//获取目的口岸分页
export const getPortList = async (params) => {
  return await request.get({ url: `/system/port/get-simple-list`, params })
}
//作废
export const batchFinishPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-finish?contractIdList=${data}`
  })
}
//整单完成
export const batchDonePurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-done?contractIdList=${data}`
  })
}
//反作废
export const batchRollBackFinishPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-rollback-finish?contractIdList=${data}`
  })
}
//下单
export const placeOrderPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-order?contractIdList=${data}`
  })
}

//退换货
export const exchangePurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-exchange?contractIdList=${data}`
  })
}
//请款
export const payPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-pay?contractIdList=${data}`
  })
}
//验货通知单
export const checkPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-check?contractIdList=${data}`
  })
}
//入库通知单
export const warehousingPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-warehousing?contractIdList=${data}`
  })
}
//变更
export const changePurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/batch-change?contractIdList=${data}`
  })
}
//回签
export const signBackPurchaseContract = async (data) => {
  return await request.put({
    url: `/scm/auxiliary-purchase-contract/sign-back`,
    data
  })
}
//获取仓库精简列表
export const getStockList = async () => {
  return await request.get({ url: `/wms/warehouse/get-simple-list` })
}
//采购合同转入库通知单
export const contractToStock = async (data) => {
  return await request.post({ url: `/scm/auxiliary-purchase-contract/to-notice`, data })
}
//打印
export const print = async (params) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/print`, params })
}
//返工重购
export const rePurchaseContract = async (data: purchaseContractVO) => {
  return await request.post({ url: `/scm/auxiliary-purchase-contract/rePurchase`, data })
}

// 获取确认来源列表
export const getAuxiliaryConfirmSource = async (params) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/confirm-source`, params })
}

// 确认
export const changeConfirm = async (id) => {
  return await request.put({ url: `/scm/auxiliary-purchase-contract/confirm?id=${id}` })
}

//变更提交
export const auxiliaryChange = async (data) => {
  return await request.put({ url: `/scm/auxiliary-purchase-contract/auxiliary-change`, data })
}

//变更列表页
export const auxiliaryChangeList = async (params) => {
  return await request.get({
    url: `/scm/auxiliary-purchase-contract/auxiliary-change-page`,
    params
  })
}
//变更详情

export const auxiliaryChangeInfo = async (id) => {
  return await request.get({
    url: `/scm/auxiliary-purchase-contract/auxiliary-change-detail?id=${id}`
  })
}
