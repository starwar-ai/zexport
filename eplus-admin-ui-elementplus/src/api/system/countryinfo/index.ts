import request from '@/config/axios'

export interface CountryInfoVO {
  id: number
  name: string
  code: string
  regionCode: string
  regionName: string
}

// 查询国家信息分页
export const getCountryInfoPage = async () => {
  return await request.get({ url: `/infra/country-info/get` })
}
// 查询收款方式
export const getSettlementInfoPage = async () => {
  return await request.get({ url: `/infra/settlement/list` })
}

// 查询国家信息详情
export const getCountryInfo = async (id: number) => {
  return await request.get({ url: `/infra/country-info/get?id=` + id })
}

// 新增国家信息
export const createCountryInfo = async (data: CountryInfoVO) => {
  return await request.post({ url: `/infra/country-info/create`, data })
}

// 修改国家信息
export const updateCountryInfo = async (data: CountryInfoVO) => {
  return await request.put({ url: `/infra/country-info/update`, data })
}

// 删除国家信息
export const deleteCountryInfo = async (id: number) => {
  return await request.delete({ url: `/infra/country-info/delete?id=` + id })
}

// 导出国家信息 Excel
export const exportCountryInfo = async (params) => {
  return await request.download({ url: `/infra/country-info/export-excel`, params })
}
