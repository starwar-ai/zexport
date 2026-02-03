import request from '@/config/axios'

export interface PaymentItemVO {
  id: number
  code: string
  name: string
  auditStatus: number
  dateType: number
  duration: number
}

// 查询付款方式分页
export const getPaymentItemPage = async (params) => {
  return await request.get({ url: `/system/payment-item/page`, params })
}
export const getPaymentItemSimpleList = async (params) => {
  return await request.get({ url: `/system/payment-item/get-simple-list`, params })
}

// 查询付款方式详情
export const getPaymentItem = async (id: number) => {
  return await request.get({ url: `/system/payment-item/get?id=` + id })
}

// 新增付款方式
export const createPaymentItem = async (data: PaymentItemVO) => {
  return await request.post({ url: `/system/payment-item/create`, data })
}

// 修改付款方式
export const updatePaymentItem = async (data: PaymentItemVO) => {
  return await request.put({ url: `/system/payment-item/update`, data })
}

// 删除付款方式
export const deletePaymentItem = async (id: number) => {
  return await request.delete({ url: `/system/payment-item/delete?id=` + id })
}

// 导出付款方式 Excel
export const exportPaymentItem = async (params) => {
  return await request.download({ url: `/system/payment-item/export-excel`, params })
}
