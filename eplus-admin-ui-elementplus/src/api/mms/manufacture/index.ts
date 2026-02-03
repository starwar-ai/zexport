import request from '@/config/axios'

// 查询分页
export const getManufacturePage = async (params) => {
  return await request.get({ url: `/mms/manufacture/page`, params })
}
//新增
export const createManufacture = async (data) => {
  return await request.post({ url: `/mms/manufacture/create`, data })
}

export const getManufactureInfo = async (params) => {
  return await request.get({ url: `/mms/manufacture/detail`, params })
}

export const getManufactureTree = async () => {
  return await request.get({ url: `/mms/manufacture/get-simple-list` })
}

// 修改
export const updateManufacture = async (data) => {
  return await request.put({ url: `/mms/manufacture/update`, data })
}

// 删除
export const deleteManufacture = async (id: number) => {
  return await request.delete({ url: `/mms/manufacture/delete?id=` + id })
}

// 导出Excel
export const exportManufacture = async (params) => {
  return await request.download({ url: `/mms/manufacture/export-excel`, params })
}
export const handleManufacture = async (type, params) => {
  return await request.post({ url: `/mms/manufacture/${type}`, params })
}
// unfinish
// finish
// done
//done-batch
