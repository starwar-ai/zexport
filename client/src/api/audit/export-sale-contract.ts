import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/sms/export/sale-contract/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/sms/export/sale-contract/reject', data: data })
}
//变更通过审核
export const changeApprove = async (data) => {
  return await request.put({ url: '/sms/export/sale-contract/change-approve', data: data })
}
//变更审核不通过
export const changeReject = async (data) => {
  return await request.put({ url: '/sms/export/sale-contract/change-reject', data: data })
}
