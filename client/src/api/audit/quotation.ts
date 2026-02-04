import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/sms/quotation/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/sms/quotation/reject', data: data })
}
