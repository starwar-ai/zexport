import request from '@/config/axios'

export interface NoticeVO {
  id: number
  code: string
  stockNoticeType: number
  stockNoticeStatus: number
  stockNoticeTime: Date
  purchaseContractId: number
  purchaseContractCode: string
  saleContractId: number
  saleContractCode: string
  warehouseId: number
  warehouseName: string
  expectDate: Date
  purchaserId: number
  purchaserDeptId: number
  salesId: number
  salesDeptId: number
  companyId: number
  companyName: string
  totalVolume: number
  totalWeight: any
  printFlag: number
  printTimes: number
  remark: string
}

// 查询分页
export const getNoticePage = async (params) => {
  return await request.get({ url: `/wms/stockNotice/page`, params })
}

// 查询详情
export const getNotice = async (params) => {
  return await request.get({ url: `/wms/stockNotice/detail`, params })
}

//审核模块的获取详情
export const getAuditNotice = async (params) => {
  return await request.get({ url: '/wms/stockNotice/audit-detail', params })
}

// 新增
export const createNotice = async (data) => {
  return await request.post({ url: `/wms/stockNotice/create`, data })
}

// 修改
export const updateNotice = async (data) => {
  return await request.put({ url: `/wms/stockNotice/update`, data })
}

// 删除
export const deleteNotice = async (id: number) => {
  return await request.delete({ url: `/wms/stockNotice/delete?id=` + id })
}

// 导出 Excel
export const exportNotice = async (params) => {
  return await request.download({ url: `/wms/stockNotice/export-excel`, params })
}

export const toBill = async (id: number) => {
  return await request.get({ url: `/wms/stockNotice/toBill?id=` + id })
}

export const factoryToBill = async (id: number) => {
  return await request.get({ url: `/wms/stockNotice/factory-toBill?id=` + id })
}

export const containerTransportationExportExcel = async (params) => {
  return await request.download({
    url: `/wms/stockNotice/container-transportation-export-excel`,
    params,
    neverLoading: true
  })
}
//打印
export const print = async (params) => {
  return await request.get({ url: `/wms/stockNotice/print`, params })
}

export const getSimpleStock = async (params) => {
  return await request.get({ url: `/wms/stock/get-simple-stock`, params })
}

export const approveApi = async (data) => {
  return await request.put({ url: '/wms/stockNotice/approve', data: data })
}
// 审核不通过
export const rejectApi = async (data) => {
  return await request.put({ url: '/wms/stockNotice/reject', data: data })
}

export const closeApi = async (id: number) => {
  return await request.put({ url: `/wms/stockNotice/close?id=` + id })
}
