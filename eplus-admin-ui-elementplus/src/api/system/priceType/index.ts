import request from '@/config/axios'

export interface PriceTypeVO {
  id: number
  typeName: string
  typeDesc: string
}

// 查询价格条款分页
export const getPriceTypePage = async (params) => {
  return await request.get({ url: `/system/price-type/page`, params })
}

// 查询价格条款详情
export const getPriceType = async (id: number) => {
  return await request.get({ url: `/system/price-type/get?id=` + id })
}

// 新增价格条款
export const createPriceType = async (data: PriceTypeVO) => {
  return await request.post({ url: `/system/price-type/create`, data })
}

// 修改价格条款
export const updatePriceType = async (data: PriceTypeVO) => {
  return await request.put({ url: `/system/price-type/update`, data })
}

// 删除价格条款
export const deletePriceType = async (id: number) => {
  return await request.delete({ url: `/system/price-type/delete?id=` + id })
}

// 导出价格条款 Excel
export const exportPriceType = async (params) => {
  return await request.download({ url: `/system/price-type/export-excel`, params })
}