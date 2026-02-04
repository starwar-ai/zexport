import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/fms/payment/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/fms/payment/reject', data: data })
}

export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/fms/payment/audit/page', params })
}
