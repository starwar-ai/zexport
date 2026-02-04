import request from '@/config/axios'

export type CompanyPathVO = {
  id: number
  path: string
  status: number
  description: string
}

//获取订单径精简列表
export const getCompanyPathSimple = (params) => {
  return request.get({ url: '/company/path/get-simple-list', params })
}

//获取订单径分页
export const getCompanyPathPage = (params) => {
  return request.get({ url: '/company/path/page', params })
}
//订单路径详情
export const getCompanyPath = async (id) => {
  return await request.get({ url: `/company/path/detail?id=` + id })
}
// 新增订单路径
export const createCompanyPath = async (data) => {
  return await request.post({ url: `/company/path/create`, data })
}

// 修改订单路径
export const updateCompanyPath = async (data) => {
  return await request.put({ url: `/company/path/update`, data })
}

// 删除订单路径
export const deleteCompanyPath = async (id: number) => {
  return await request.delete({ url: `/company/path/delete?id=` + id })
}

//导出订单路径
export const exportCompanyPath = (params) => {
  return request.download({ url: '/company/path/export-excel', params })
}
