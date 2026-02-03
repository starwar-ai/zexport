import request from '@/config/axios'

export interface RegistrationVO {
  id: number
}
//查询客户认领列表
export const getCustClaimPage = async (params) => {
  return await request.get({ url: `/fms/cust-claim/list`, params })
}
//查询客户认领详情
export const getCustClaim = async (params) => {
  return await request.get({ url: `/fms/cust-claim/claim`, params })
}
//查询客户认领详情
export const getCustClaimDetail = async (params) => {
  return await request.get({ url: `/fms/cust-claim/detail`, params })
}
//认领提交
export const saveCustClaim = async (data: RegistrationVO) => {
  return await request.post({ url: `/fms/cust-claim/save`, data })
}

//认领提交
export const searchCustClaim = async (params) => {
  return await request.get({ url: `/fms/cust-claim/claim-item`, params })
}

//查询汇率
export const getDateRate = async (params) => {
  return await request.get({ url: `/infra/rate/get-by-date?dailyCurrDate=${params}` })
}

// 取消认领
export const cancelClaim = async (id) => {
  return await request.put({ url: `/fms/cust-claim/cancel?id=${id}` })
}
