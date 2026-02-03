import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/scm/purchase-plan/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/scm/purchase-plan/reject', data: data })
}

export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/scm/vender/audit/page', params })
}
// 通过审核
export const examineRegistrationApprove = async (data) => {
  return await request.put({ url: '/scm/purchase-registration/approve', data: data })
}
// 审核不通过
export const examineRegistrationReject = async (data) => {
  return await request.put({ url: '/scm/purchase-registration/reject', data: data })
}

// 包材采购计划
export const auxiliaryApprove = async (data) => {
  return await request.put({ url: '/scm/auxiliary-purchase-plan/approve', data: data })
}

// 包材采购计划
export const auxiliaryReject = async (data) => {
  return await request.put({ url: '/scm/auxiliary-purchase-plan/reject', data: data })
}
