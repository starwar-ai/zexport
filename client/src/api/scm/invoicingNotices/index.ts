import request from '@/config/axios'

export interface RegistrationVO {
  id: number
}
//查询开票通知列表
export const getInvoicingNoticesPage = async (params) => {
  return await request.get({ url: `/scm/invoicing-notices/page`, params })
}
//查询开票通知详情
export const getInvoicingNoticesDetail = async (params) => {
  return await request.get({ url: `/scm/invoicing-notices/detail`, params })
}
export const getInvoicingNoticesAuditDetail = async (params) => {
  return await request.get({ url: `/scm/invoicing-notices/audit-detail`, params })
}

//认领提交
export const saveInvoicingNotices = async (data: RegistrationVO) => {
  return await request.post({ url: `/fms/cust-claim/save`, data })
}
//打印
export const invoicingNoticesPrint = async (type, params) => {
  return await request.get({ url: `/scm/invoicing-notices/${type}`, params })
}
export const invoicingNoticesApprove = async (data) => {
  return await request.put({ url: `/scm/invoicing-notices/approve`, data })
}
export const invoicingNoticesReject = async (data) => {
  return await request.put({ url: `/scm/invoicing-notices/reject`, data })
}

export const invoicingNoticesClose = async (params) => {
  return await request.put({ url: `/scm/invoicing-notices/close`, params })
}

export const invoicingNoticesUpdate = async (data) => {
  return await request.put({ url: `/scm/invoicing-notices/update`, data })
}

//获取公司主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}
