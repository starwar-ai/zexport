import request from '@/config/axios'

// 查询分页
export const getBrandPage = async (params) => {
  return await request.get({ url: `/pms/brand/page`, params })
}
export const getBrandSimpleList = async () => {
  return await request.get({ url: `/pms/brand/get-simple-list` })
}

//新增
export const createBrand = async (data) => {
  return await request.post({ url: `/pms/brand/create`, data })
}

export const getBrandInfo = async (params) => {
  return await request.get({ url: `/pms/brand/detail`, params })
}

// 修改
export const updateBrand = async (data) => {
  return await request.put({ url: `/pms/brand/update`, data })
}

// 删除
export const deleteBrand = async (id: number) => {
  return await request.delete({ url: `/pms/brand/delete?id=` + id })
}

// 导出Excel
export const exportPaymentApp = async (params) => {
  return await request.download({ url: `/pms/brand/export-excel`, params })
}
