import request from '@/config/axios'

// 查询分页
export const getStocktakePage = async (params) => {
  return await request.get({ url: `/wms/stocktake/page`, params })
}

// 查询详情
export const getStocktake = async (params) => {
  return await request.get({ url: `/wms/stocktake/detail`, params })
}

// 新增
export const createStocktake = async (data) => {
  return await request.post({ url: `/wms/stocktake/create`, data })
}

// 修改
export const updateStocktake = async (data) => {
  return await request.put({ url: `/wms/stocktake/update`, data })
}

export const cancelStocktake = async (id) => {
  return await request.get({ url: `/wms/stocktake/cancel?id=${id}` })
}

// 删除
export const deleteStocktake = async (id: number) => {
  return await request.delete({ url: `/wms/stocktake/delete?id=` + id })
}

// 导出 Excel
export const exportStocktake = async (params) => {
  return await request.download({ url: `/wms/stocktake/export-excel`, params })
}

//提交
export const submitStocktake = async (id) => {
  return await request.put({ url: `/wms/stocktake/submit?id=${id}` })
}
//仓库列表
export const warehouseList = async () => {
  return await request.get({ url: `wms/warehouse/get-simple-list` })
}

export const queryBatch = async (data) => {
  return await request.post({ url: `/wms/stock/queryBatch`, data })
}

//通过
export const approveStocktake = async (data) => {
  return await request.put({ url: `/wms/stocktake/approve`, data })
}
//不通过
export const rejectStocktake = async (data) => {
  return await request.put({ url: `/wms/stocktake/reject`, data })
}

//开始盘点
export const stocktakeCounting = async (data) => {
  return await request.put({ url: `/wms/stocktake/counting`, data })
}
//盘点录入
export const stocktakeComplete = async (data) => {
  return await request.put({ url: `/wms/stocktake/complete`, data })
}

export const adjustmentPage = async (params) => {
  return await request.get({ url: `/wms/adjustment/page`, params })
}

export const adjustmentDetail = async (id) => {
  return await request.get({ url: `/wms/adjustment/detail?id=${id}` })
}

export const exportDetail = async (params) => {
  return await request.download({ url: `/wms/stocktake/export-detail-excel`, params })
}
