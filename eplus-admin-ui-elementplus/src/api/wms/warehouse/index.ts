import request from '@/config/axios'
// 查询分页
export const getWarehousePage = async (params) => {
  return await request.get({ url: `/wms/warehouse/page`, params })
}
//新增
export const createWarehouse = async (data) => {
  return await request.post({ url: `/wms/warehouse/create`, data })
}

export const getWarehouseInfo = async (params) => {
  return await request.get({ url: `/wms/warehouse/detail`, params })
}
// 修改
export const updateWarehouse = async (data) => {
  return await request.put({ url: `/wms/warehouse/update`, data })
}
// enable  启用  disable  停用
export const updateWarehouseStatus = async (type, id) => {
  return await request.put({ url: `/wms/warehouse/${type}/${id}` })
}
// 设置默认仓库
export const setDefaultWarehouse = async (id) => {
  return await request.put({ url: `/wms/warehouse/set-default/${id}` })
}



// 删除
// export const deleteWarehouse = async (type, id: number) => {
//   return await request.delete({ url: `/wms/warehouse/${type}/${id}` })
// }

// 导出Excel
export const exportWarehouse = async (params) => {
  return await request.download({ url: `/wms/warehouse/export-excel`, params })
}
