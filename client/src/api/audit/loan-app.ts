import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/oa/loan-app/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/oa/loan-app/reject', data: data })
}

export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/oa/loan-app/audit/page', params })
}
