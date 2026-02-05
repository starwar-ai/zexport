import request from '@/config/axios'

export interface ExportSaleContractVO {
  id: number
  code: string
  companyId: number
  companyName: string
  orderPath: string
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
export const getExportSaleContractPage = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/page`, params })
}

// 查询外销合同信息详情
export const getExportSaleContract = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/detail`, params })
}

//审核模块的获取详情
export const getAuditExportSaleContract = async (params) => {
  return await request.get({ url: '/sms/export/sale-contract/audit-detail', params })
}

// 新增外销合同信息
export const createExportSaleContract = async (data: ExportSaleContractVO) => {
  return await request.post({ url: `/sms/export/sale-contract/create`, data })
}

// 修改外销合同信息
export const updateExportSaleContract = async (data: ExportSaleContractVO) => {
  return await request.put({ url: `/sms/export/sale-contract/update`, data })
}

// 删除外销合同信息
export const deleteExportSaleContract = async (id: number) => {
  return await request.delete({ url: `/sms/export/sale-contract/delete?id=` + id })
}

// 外销合同提交
export const submitExportSaleContract = async (id) => {
  return await request.put({ url: `/sms/export/sale-contract/submit?contactId=${id}` })
}

// 导出Excel
export const exportExportSaleContract = async (params) => {
  return await request.download({ url: `/sms/export/sale-contract/export-excel`, params })
}
//提交变更
export const submitChange = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/change`, data })
}
// 查询分页
export const getExportSaleContractChangePage = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/change-page`, params })
}
// 查询精简分页
export const getSaleContractSimple = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/get-simple-list`, params })
}
// 下推采购计划
export const toPurchasePlan = async (params) => {
  return await request.post({ url: `/sms/export/sale-contract/to-purchase-plan`, params })
}
// 回签
export const signBack = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/sign-back`, data })
}
// 作废
export const close = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/close`, data })
}
// 完成单据
export const orderDone = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/order-done?id=${data}` })
}
//打印
export const print = async (type, params) => {
  return await request.get({
    url: `/sms/${type === 1 ? 'export' : type === 2 ? 'domestic' : 'factory'}/sale-contract/print`,
    params
  })
}
//获取可销售明细库存信息
export const getCustSaleContractSimple = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/get-cust-info-list`, params })
}

// 获取确认来源列表
export const getConfirmSource = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/confirm-source`, params })
}

// 确认
export const changeConfirm = async (id) => {
  return await request.put({ url: `/sms/export/sale-contract/confirm?id=${id}` })
}

//更新设计稿
export const updateDesign = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/update-design`, data })
}
// 费用详情
export const saleContractDetailFee = async (id) => {
  return await request.get({ url: `/sms/export/sale-contract/detail-fee?id=${id}` })
}
export const saleContractAuditDetailFee = async (id) => {
  return await request.get({ url: `/sms/export/sale-contract/audit-detail-fee?id=${id}` })
}

export const saveAllocation = async (data) => {
  return await request.put({ url: `/sms/sale-auxiliary-allocation/allocation`, data })
}

export const cancelAllocation = async (id) => {
  return await request.put({ url: `/sms/sale-auxiliary-allocation/allocation-cancel?itemId=${id}` })
}

export const allocationInfo = async (params) => {
  return await request.get({ url: `/sms/sale-auxiliary-allocation/allocation-detail`, params })
}

export const batchToPurchasePlan = async (params) => {
  return await request.post({ url: `/sms/export/sale-contract/batch-to-purchase-plan`, params })
}
//查询收款账户
export const getCollectionAccount = async (params) => {
  return await request.get({ url: `/crm/collection-account/get-by-cust`, params })
}

export const checkContractStatus = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/check-contract-status`, params })
}

// 根据code查询id
export const getContractIdByCode = async (code) => {
  return await request.get({ url: `/sms/export/sale-contract/get-id-by-code?code=${code}` })
}

export const reLock = async (data) => {
  return await request.put({ url: `/sms/export/sale-contract/re-lock`, data })
}

// 导出外销合同 Excel 详细信息（单个）
export const exportSaleContract = async (params) => {
  return await request.download({
    url: `/sms/export/sale-contract/export-excel-detail`,
    params,
    neverLoading: true
  })
}

// 批量导出外销合同 Excel 详细信息
export const batchExportSaleContract = async (data: {
  ids: number[]
  reportCode: string
  exportType: string
}) => {
  return await request.download({
    url: `/sms/export/sale-contract/batch-export-excel?reportCode=${data.reportCode}&exportType=${data.exportType}`,
    method: 'post',
    data: data.ids
  })
}

// 批量打印外销合同
export const batchPrintSaleContract = async (data: {
  ids: number[]
  reportCode: string
  printType: string
}) => {
  return await request.post({
    url: `/sms/export/sale-contract/batch-print?reportCode=${data.reportCode}&printType=${data.printType}`,
    data: data.ids
  })
}

export const exportSaleContractWord = async (type, params) => {
  return await request.download({
    // domestic  内销   export 外销
    url: `/sms/${type === 1 ? 'export' : 'domestic'}/sale-contract/export-word`,
    params
  })
}

export const genContract = async (id) => {
  return await request.put({ url: `/sms/export/sale-contract/gen-contract?contactId=${id}` })
}

export const genContractDel = async (id) => {
  return await request.put({ url: `/sms/export/sale-contract/delete-gen-contract?contactId=${id}` })
}

export const updateCollectionPlanApi = async (data, apiType) => {
  return await request.put({ url: `/sms/${apiType}/sale-contract/update-collection-plan`, data })
}
