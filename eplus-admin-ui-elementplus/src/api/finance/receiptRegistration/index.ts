import request from '@/config/axios'

export interface RegistrationVO {
  id: number
}

// 查询财务付款信息分页
export const getRegistrationPage = async (params) => {
  return await request.get({ url: `/fms/bank-registration/page`, params })
}

// 查询财务付款信息详情
export const getRegistration = async (params) => {
  return await request.get({ url: `/fms/bank-registration/detail`, params })
}

//审核模块的获取详情
export const getAuditRegistration = async (params) => {
  return await request.get({ url: '/fms/bank-registration/audit-detail', params })
}

// 新增财务付款信息
export const createRegistration = async (data: RegistrationVO) => {
  return await request.post({ url: `/fms/bank-registration/create`, data })
}

// 修改财务付款信息
export const updateRegistration = async (data: RegistrationVO) => {
  return await request.put({ url: `/fms/bank-registration/update`, data })
}

// 删除财务付款信息
export const deleteRegistration = async (id: number) => {
  return await request.delete({ url: `/fms/bank-registration/delete?id=` + id })
}

// 导出财务付款信息 Excel
export const exportRegistration = async (params) => {
  return await request.download({ url: `/fms/bank-registration/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//财务付款提交
export const submitRegistration = async (id) => {
  return await request.put({ url: `/fms/bank-registration/submit?receiptId=${id}` })
}

//支付确认
export const getBankRegistration = async (params?) => {
  return await request.get({ url: `/fms/bank-registration/get-simple-msg`, params })
}

export const getBankPocList = async (params?) => {
  return await request.get({ url: `/fms/bank-registration/get-bank-poc`, params })
}
