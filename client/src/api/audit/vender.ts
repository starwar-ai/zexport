import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/scm/vender/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/scm/vender/reject', data: data })
}

export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/scm/vender/audit/page', params })
}

export const examineChangeApprove = async (data) => {
  return await request.put({ url: '/scm/vender/change-approve', data: data })
}
// 审核不通过
export const examineChangeReject = async (data) => {
  return await request.put({ url: '/scm/vender/change-reject', data: data })
}
