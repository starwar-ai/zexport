import request from '@/config/axios'

export interface TravelAppVO {
  id: number
  purpose: string
  dest: string
  result: number
  startTime: Date
  endTime: Date
  duration: number
  transportationType: string
  companions: string
  createTime: Date
}

// 查询出差申请分页
export const getTravelAppPage = async (params) => {
  return await request.get({ url: `/oa/travel-app/page`, params })
}

// 查询出差申请详情
export const getTravelApp = async (id: number) => {
  return await request.get({ url: `/oa/travel-app/get?id=` + id })
}

// 新增出差申请
export const createTravelApp = async (data: TravelAppVO) => {
  return await request.post({ url: `/oa/travel-app/create`, data })
}

// 修改出差申请
export const updateTravelApp = async (data: TravelAppVO) => {
  return await request.put({ url: `/oa/travel-app/update`, data })
}

// 删除出差申请
export const deleteTravelApp = async (id: number) => {
  return await request.delete({ url: `/oa/travel-app/delete?id=` + id })
}

// 导出出差申请 Excel
export const exportTravelApp = async (params) => {
  return await request.download({ url: `/oa/travel-app/export-excel`, params })
}
//获取客户列表
export const getSimpleList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}
