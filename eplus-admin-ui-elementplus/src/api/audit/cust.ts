import request from '@/config/axios'

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/crm/cust/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/crm/cust/reject', data: data })
}
export const getMyProcessInstancePage = async (params) => {
  return await request.get({ url: '/crm/cust/audit/page', params })
}


// 客户变更通过审核
export const examineChangeApprove = async (data) => {
  return await request.put({ url: '/crm/cust/change-approve', data: data })
}
// 客户变更审核不通过
export const examineChangeReject = async (data) => {
  return await request.put({ url: '/crm/cust/change-reject', data: data })
}

