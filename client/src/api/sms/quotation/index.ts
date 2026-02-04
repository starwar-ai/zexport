import request from '@/config/axios'

export type QuotationVO = {
  id: number
  companyId: number
  companyName: string
  custId: number
  custCode: string
  custName: string
  isNewCust: number
  settlementTermType: string
  custPoc: object
  countryId: number
  departurePortId: number
  departurePortName: string
  validPeriod: string
  manager: object
}

//获取报价单管理分页
export const getQuotationPage = (params) => {
  return request.get({ url: '/sms/quotation/page', params })
}

export const getQuotationItemPage = (params) => {
  return request.get({ url: '/sms/quotation/item-page', params })
}

//报价单管理详情
export const getQuotation = async (id) => {
  return await request.get({ url: `/sms/quotation/detail?id=` + id })
}
// 新增报价单管理
export const createQuotation = async (data: QuotationVO) => {
  return await request.post({ url: `/sms/quotation/create`, data })
}

// 修改报价单管理
export const updateQuotation = async (data: QuotationVO) => {
  return await request.put({ url: `/sms/quotation/update`, data })
}

// 删除报价单管理
export const deleteQuotation = async (id: number) => {
  return await request.delete({ url: `/sms/quotation/delete?id=` + id })
}

//导出报价单管理
export const getMenuList = (params) => {
  return request.get({ url: '/sms/quotation/getMenuList', params })
}

//审核模块的获取详情
export const getAuditQuotation = async (params) => {
  return await request.get({ url: '/sms/quotation/audit-detail', params })
}

// 提交
export const submitQuotation = async (id) => {
  return await request.put({ url: `/sms/quotation/submit?quotationId=${id}` })
}

// 作废
export const finish = async (params) => {
  return await request.put({ url: `/sms/quotation/finish`, params })
}

// 导出Excel
export const exportQuotation = async (params) => {
  return await request.download({
    url: `/sms/quotation/export-excel`,
    params,
    neverLoading: true
  })
}

export const printQuotation = async (params) => {
  return await request.get({
    url: `/sms/quotation/print`,
    params
  })
}
