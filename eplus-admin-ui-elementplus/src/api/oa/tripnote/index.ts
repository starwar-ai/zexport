import request from '@/config/axios'

export interface TripNoteVO {
  id: number
  purpose: string
  dest: string
  status: number
  startTime: Date
  endTime: Date
  duration: number
  transportationType: string
  companions: string
}

// 查询出差申请分页
export const getTripNotePage = async (params) => {
  return await request.get({ url: `/oa/trip-note/page`, params })
}

// 查询出差申请详情
export const getTripNote = async (id: number) => {
  return await request.get({ url: `/oa/trip-note/get?id=` + id })
}

// 新增出差申请
export const createTripNote = async (data: TripNoteVO) => {
  return await request.post({ url: `/oa/trip-note/create`, data })
}

// 修改出差申请
export const updateTripNote = async (data: TripNoteVO) => {
  return await request.put({ url: `/oa/trip-note/update`, data })
}

// 删除出差申请
export const deleteTripNote = async (id: number) => {
  return await request.delete({ url: `/oa/trip-note/delete?id=` + id })
}

// 导出出差申请 Excel
export const exportTripNote = async (params) => {
  return await request.download({ url: `/oa/trip-note/export-excel`, params })
}
