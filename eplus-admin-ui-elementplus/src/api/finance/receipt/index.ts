import request from '@/config/axios'

export interface ReceiptVO {
  id: number
}

// 查询财务付款信息分页
export const getReceiptPage = async (params) => {
  return await request.get({ url: `/fms/receipt/page`, params })
}

// 查询财务付款信息详情
export const getReceipt = async (params) => {
  return await request.get({ url: `/fms/receipt/detail`, params })
}

//审核模块的获取详情
export const getAuditReceipt = async (params) => {
  return await request.get({ url: '/fms/receipt/audit-detail', params })
}

// 新增财务付款信息
export const createReceipt = async (data: ReceiptVO) => {
  return await request.post({ url: `/fms/receipt/create`, data })
}

// 修改财务付款信息
export const updateReceipt = async (data: ReceiptVO) => {
  return await request.put({ url: `/fms/receipt/update`, data })
}

// 删除财务付款信息
export const deleteReceipt = async (id: number) => {
  return await request.delete({ url: `/fms/receipt/delete?id=` + id })
}

// 导出财务付款信息 Excel
export const exportReceipt = async (params) => {
  return await request.download({ url: `/fms/receipt/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//财务付款提交
export const submitReceipt = async (id) => {
  return await request.put({ url: `/fms/receipt/submit?receiptId=${id}` })
}

//支付确认
export const confirmReceipt = async (id) => {
  return await request.put({ url: `/fms/receipt/confirm?receiptId=${id}` })
}
