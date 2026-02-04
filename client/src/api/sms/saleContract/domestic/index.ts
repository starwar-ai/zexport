import request from '@/config/axios'

export interface DomesticSaleContractVO {
  id: number
  code: string
  companyId: number
  companyName: string
  companyPath: object
  custId: number
  custCode: string
  custName: string
  currency: string
  settlementTermType: number
  settlementId: number
  settlementName: string
  custCountryId: number
  custCountryName: string
  custAreaName: string
  custPo: string
  agentFlag: number
  collectedCustId: number
  collectedCustCode: string
  collectedCustName: string
  receiveCustId: number
  receiveCustCode: string
  receiveCustName: string
  sales: string
  annex: string
  inputDate: Date
  tradeCountryId: number
  tradeCountryName: string
  departureCountryId: number
  departureCountryName: string
  departurePortId: number
  departurePortName: string
  destinationPortId: number
  destinationPortName: string
  transportType: number
  twentyFootContainerNum: number
  fortyFootCabinetNum: number
  bulkHandlingFee: JSON
  trailerFee: JSON
  estimatedTotalFreight: JSON
  bookingFlag: number
  commission: JSON
  platformFee: JSON
  insuranceFee: JSON
  sinosureFee: JSON
  additionAmount: JSON
  deductionAmount: JSON
  inspectionFee: JSON
  estimatedPackingMaterials: JSON
  accessoriesPurchaseTotal: JSON
  totalBoxes: number
  totalGrossweight: JSON
  ototalWeight: JSON
  totalVolume: JSON
  totalGoodsValue: JSON
  totalPurchase: JSON
  totalVatRefund: JSON
  totalQuantity: number
  orderGrossProfit: JSON
  grossProfitMargin: JSON
  receivableExchange: JSON
  saleType: number
  processInstanceId: number
}

// 查询外销合同信息分页
export const getDomesticSaleContractPage = async (params) => {
  return await request.get({ url: `/sms/domestic/sale-contract/page`, params })
}

// 查询外销合同信息详情
export const getDomesticSaleContract = async (params) => {
  return await request.get({ url: `/sms/domestic/sale-contract/detail`, params })
}

//审核模块的获取详情
export const getAuditDomesticSaleContract = async (params) => {
  return await request.get({ url: '/sms/domestic/sale-contract/audit-detail', params })
}

// 新增外销合同信息
export const createDomesticSaleContract = async (data: DomesticSaleContractVO) => {
  return await request.post({ url: `/sms/domestic/sale-contract/create`, data })
}

// 修改外销合同信息
export const updateDomesticSaleContract = async (data: DomesticSaleContractVO) => {
  return await request.put({ url: `/sms/domestic/sale-contract/update`, data })
}

// 删除外销合同信息
export const deleteDomesticSaleContract = async (id: number) => {
  return await request.delete({ url: `/sms/domestic/sale-contract/delete?id=` + id })
}

// 外销合同提交
export const submitDomesticSaleContract = async (id) => {
  return await request.put({ url: `/sms/domestic/sale-contract/submit?contactId=${id}` })
}

// 导出Excel
export const exportDomesticSaleContract = async (params) => {
  return await request.download({ url: `/sms/domestic/sale-contract/export-excel`, params })
}

// 查询分页
export const getDomesticSaleContractChangePage = async (params) => {
  return await request.get({ url: `/sms/domestic/sale-contract/change-page`, params })
}
// 下推采购计划
export const toPurchasePlan = async (params) => {
  return await request.post({ url: `/sms/domestic/sale-contract/to-purchase-plan`, params })
}
// 回签
export const signBack = async (data) => {
  return await request.put({ url: `/sms/domestic/sale-contract/sign-back`, data })
}

// 作废
export const close = async (data) => {
  return await request.put({ url: `/sms/domestic/sale-contract/close`, data })
}

// 批量查询产品库存
export const queryTotalStock = async (data) => {
  return await request.post({ url: `/wms/stock/queryTotalStock`, data })
}

//查询批次库存
export const listBatch = async (data) => {
  return await request.post({ url: `/wms/stock/listBatch`, data, neverLoading: true })
}
// 锁定库存-批量
export const createBatch = async (data) => {
  return await request.post({ url: `/wms/stock-lock/createBatch`, data })
}
// 释放库存-批量
export const cancelBatch = async (code) => {
  return await request.get({ url: `/wms/stock-lock/cancelBatch?saleContractCode=${code}` })
}
//根据code查询产品详情

export const getSkuInfo = async (code) => {
  return await request.get({ url: `pms/sku/get-simple-detail?skuCode=${code}` })
}

export const outNoticeMid = async (ids) => {
  return await request.get({ url: `/sms/domestic/sale-contract/out-notice-mid?ids=${ids}` })
}

export const transformNotice = async (data) => {
  return await request.post({ url: `/sms/domestic/sale-contract/transform-notice`, data })
}
