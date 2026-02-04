import request from '@/config/axios'

// 通过审核
export const approvePaymentApp = async (data) => {
  return await request.put({ url: 'oa/payment-app/approve', data: data })
}
// 审核不通过
export const rejectPaymentApp = async (data) => {
  return await request.put({ url: 'oa/payment-app/reject', data: data })
}
