import request from '@/config/axios'

// 查询分页
export const getPaymentAppPage = async (params) => {
  return await request.get({ url: `/oa/payment-app/page`, params })
}
//新增
export const createPaymentApp = async (data) => {
  return await request.post({ url: `/oa/payment-app/create`, data })
}

export const getPaymentAppInfo = async (params) => {
  return await request.get({ url: `/oa/payment-app/detail`, params })
}
export const getAuditPaymentAppInfo = async (params) => {
  return await request.get({ url: `/oa/payment-app/audit-detail`, params })
}

// 修改
export const updatePaymentApp = async (data) => {
  return await request.put({ url: `oa/payment-app/update`, data })
}

// 删除
export const deletePaymentApp = async (id: number) => {
  return await request.delete({ url: `oa/payment-app/delete?id=` + id })
}

// 导出Excel
export const exportPaymentApp = async (params) => {
  return await request.download({ url: `oa/payment-app/export-excel`, params })
}

//提交
export const submitPaymentApp = async (id) => {
  return await request.put({ url: `oa/payment-app/submit?paymentAppId=${id}` })
}

//通过
export const approvePaymentApp = async (data) => {
  return await request.put({ url: `oa/payment-app/approve`, data })
}
//不通过
export const rejectPaymentApp = async (data) => {
  return await request.put({ url: `oa/payment-app/reject`, data })
}

//查询供应商列表
export const getVenderList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list`, params })
}
//查询客户列表
export const getCustList = async (params) => {
  return await request.get({ url: `/crm/cust/get-cust-bank-list`, params })
}
// 查询历史单据
export const getHistoryList = async (type, code, codeList, companyId, prepaidFlag) => {
  return await request.get({
    url: `/oa/payment-app/get-simple-list?type=${type}&code=${code}&codeList=${codeList}&companyId=${companyId}&prepaidFlag=${prepaidFlag}`
  })
}

//打印
export const print = async (params) => {
  return await request.get({ url: `/oa/payment-app/print`, params })
}

export const shipmentList = async (params) => {
  return await request.get({ url: `/dms/shipment/get-list-by-code`, params })
}

export const auxiliaryPurchaseList = async (params) => {
  return await request.get({ url: `/scm/auxiliary-purchase-contract/get-list-by-code`, params })
}

export const sendList = async (params) => {
  return await request.get({ url: `ems/send/get-list-by-code`, params })
}

//导出excel
export const exportPoireport = async (params) => {
  return await request.download({ url: `/oa/payment-app/export-excel-send-detail`, params })
}

//打印船代费用明细
export const printShipmentFeeDetail = async (params) => {
  return await request.get({ url: `/oa/payment-app/print-shipment-fee-detail`, params })
}
