import request from '@/config/axios'

export interface PaymentVO {
  id: number
}

// 查询财务付款信息分页
export const getPaymentPage = async (params) => {
  return await request.get({ url: `/fms/payment/page`, params })
}

// 查询财务付款信息详情
export const getPayment = async (params) => {
  return await request.get({ url: `/fms/payment/detail`, params })
}

//审核模块的获取详情
export const getAuditPayment = async (params) => {
  return await request.get({ url: '/fms/payment/audit-detail', params })
}

// 新增财务付款信息
export const createPayment = async (data) => {
  return await request.post({ url: `/scm/payment-apply/create`, data })
}

// 修改财务付款信息
export const updatePayment = async (data: PaymentVO) => {
  return await request.put({ url: `/fms/payment/update`, data })
}

// 删除财务付款信息
export const deletePayment = async (id: number) => {
  return await request.delete({ url: `/fms/payment/delete?id=` + id })
}

// 导出财务付款信息 Excel
export const exportPayment = async (params) => {
  return await request.download({ url: `/fms/payment/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//财务付款提交
export const submitPayment = async (id) => {
  return await request.put({ url: `/fms/payment/submit?paymentId=${id}` })
}

//支付确认
export const confirmPayment = async (id) => {
  return await request.put({ url: `/fms/payment/confirm?paymentId=${id}` })
}
//财务付款申请详情
export const getPaymentApply = async (id) => {
  return await request.get({ url: `/scm/payment-apply/detail?id=${id}` })
}
//财务付款申请审批详情
export const getAuditPaymentApply = async (id) => {
  return await request.get({ url: `/scm/payment-apply/audit-detail?id=${id}` })
}
//财务付款更新
export const updatePaymentApply = async (data) => {
  return await request.put({ url: `/scm/payment-apply/update`, data })
}

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/scm/payment-apply/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/scm/payment-apply/reject', data: data })
}

//打印
export const print = async (params) => {
  return await request.get({ url: `/scm/payment-apply/print`, params })
}
