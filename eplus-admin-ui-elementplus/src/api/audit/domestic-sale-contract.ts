import request from '@/config/axios'

// 通过审核
export const domExamineApprove = async (data) => {
  return await request.put({ url: '/sms/domestic/sale-contract/approve', data: data })
}
// 审核不通过
export const domExamineReject = async (data) => {
  return await request.put({ url: '/sms/domestic/sale-contract/reject', data: data })
}
