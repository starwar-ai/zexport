import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/system/poi-report/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/system/poi-report/reject', data: data })
}

export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/system/poi-report/audit/page', params })
}
