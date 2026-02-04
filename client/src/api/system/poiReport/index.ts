import request from '@/config/axios'

export interface ReportVO {
  id: number
  code: string
  name: string
  path: string
  reportType: any
  sourceType: any
  sourceCode: string
  sourceName: string
}

// 查询打印模板信息分页
export const getReportPage = async (params) => {
  return await request.get({ url: `/system/poi-report/page`, params })
}

// 查询打印模板信息详情
export const getReport = async (params) => {
  return await request.get({ url: `/system/poi-report/detail`, params })
}
export const getAuditReport = async (params) => {
  return await request.get({ url: `/system/poi-report/audit-detail`, params })
}
//查询供应商列表
export const getVenderList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list`, params })
}
//查询客户列表
export const getCustList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}
// 新增打印模板信息
export const createReport = async (data: ReportVO) => {
  return await request.post({ url: `/system/poi-report/create`, data })
}
//列表提交模板信息
export const submitReport = async (reportId) => {
  return await request.put({ url: `/system/poi-report/submit?reportId=${reportId}` })
}
// 修改打印模板信息
export const updateReport = async (data: ReportVO) => {
  return await request.put({ url: `/system/poi-report/update`, data })
}

// 删除打印模板信息
export const deleteReport = async (id: number) => {
  return await request.delete({ url: `/system/poi-report/delete?id=` + id })
}
//导出excel
export const exportPoireport = async (params) => {
  return await request.download({ url: `/system/poi-report/export-excel`, params })
}

//获取账套下特定模板
export const getCompanySpecificReport = async (params) => {
  return await request.get({ url: `/system/poi-report/company-specific-report`, params })
}

export const directlyUpdate = async (data) => {
  return await request.put({ url: `/system/poi-report/directly-update`, data })
}
