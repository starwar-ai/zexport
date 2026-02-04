import request from '@/config/axios'

export interface HsdataVO {
  id: number
  ver: number
  code: string
  name: string
  unit: string
  taxRefundRate: number
  rate: number
  remark: string
  chname: string
  addrate: number
  code2: string
}

// 查询海关编码分页
export const getHsdataList = async (params) => {
  return await request.get({ url: `/pms/hsdata/page`, params })
}
export const getHsdataPage = async (params) => {
  return await request.get({ url: `/pms/hsdata/page`, params, neverLoading: true })
}

// 查询海关编码详情
export const getHsdata = async (id: number) => {
  return await request.get({ url: `/pms/hsdata/get?id=` + id })
}

// 新增海关编码
export const createHsdata = async (data: HsdataVO) => {
  return await request.post({ url: `/pms/hsdata/create`, data })
}

// 修改海关编码
export const updateHsdata = async (data: HsdataVO) => {
  return await request.put({ url: `/pms/hsdata/update`, data })
}

// 删除海关编码
export const deleteHsdata = async (id: number) => {
  return await request.delete({ url: `/pms/hsdata/delete?id=` + id })
}

// 导出海关编码 Excel
export const exportHsdata = async (params) => {
  return await request.download({ url: `/pms/hsdata/export-excel`, params })
}
