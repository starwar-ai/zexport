import request from '@/config/axios'
export interface StockForSaleQueryVO {
  skuId: number
  companyId: number
  venderCode?: string
}
// 查询分页
export const getStockPage = async (params) => {
  return await request.get({ url: `/wms/stock/pageBySku`, params })
}

// 查询详情
export const getStockInfo = async (params) => {
  return await request.get({ url: `/wms/stock/detail`, params })
}

export const getRecord = async (code) => {
  return await request.get({ url: `/wms/bill/getRecord?batchCode=${code}` })
}

export const lockList = async (id) => {
  return await request.get({
    url: `/wms/stock-lock/list?pageNo=1&pageSize=10&stockId=${id}`,
    neverLoading: true
  })
}

//根据产品编码查询批次库存信息
export const queryBatchForSale = async (data: StockForSaleQueryVO) => {
  return await request.post({ url: `/wms/stock/queryBatchForSale`, data })
}

export const importStockTemplate = () => {
  return request.download({ url: '/wms/stock-import/get-import-template' })
}

// 确认导入库存
export const confirmImportStock = async (params) => {
  return await request.post({ url: `/wms/stock/import`, params })
}
