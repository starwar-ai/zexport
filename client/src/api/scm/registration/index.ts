import request from '@/config/axios'

export interface RegistrationVO {
  id?: number
}
//查询发票列表
export const getRegistrationPage = async (params) => {
  return await request.get({ url: `/scm/purchase-registration/page`, params })
}
//查询发票详情
export const getRegistrationDetail = async (params) => {
  return await request.get({ url: `/scm/purchase-registration/detail`, params })
}
//查询发票审核页详情
export const getRegistrationAuditDetail = async (params) => {
  return await request.get({ url: `/scm/purchase-registration/audit-detail`, params })
}
//发票提交
// export const saveRegistration = async (data: RegistrationVO) => {
//   return await request.post({ url: `/fms/cust-claim/save`, data })
// }
export const saveRegistration = async (data: RegistrationVO) => {
  return await request.post({ url: `/scm/purchase-registration/create`, data })
}
export const updateRegistration = async (data) => {
  return await request.put({ url: `/scm/purchase-registration/update`, data })
}
//发票审核
export const getRegistrationItemList = async (params) => {
  return await request.get({ url: `/scm/invoicing-notices/get-list`, params })
}
//提交审核
export const submitRegistration = async (id) => {
  return await request.put({ url: `/scm/purchase-registration/submit?paymentId=${id}` })
}
//获得开票通知分页
export const getInvoicingNoticesPage = async (params) => {
  return await request.get({ url: `/scm/invoicing-notices/page`, params })
}

// 根据发票号查询供应商
export const getVenderList = async (code) => {
  return await request.get({
    url: `/scm/invoicing-notices/getVenderListByShipInvoiceCode?shipInvoiceCode=${code}`
  })
}

export const reviewApi = async (data) => {
  return await request.put({ url: `/scm/purchase-registration/batch-review`, data })
}

// 导出Excel
export const exportRegistration = async (params) => {
  return await request.download({
    url: `/scm/purchase-registration/export-excel`,
    params,
    neverLoading: true
  })
}

export const registrationClose = async (id) => {
  return await request.put({ url: `/scm/purchase-registration/close?id=${id}` })
}
