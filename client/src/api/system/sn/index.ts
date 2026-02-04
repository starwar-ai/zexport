import request from '@/config/axios'

export interface SnVO {
  id: number
  type: string
  sn: number
  length: number
}

// 查询序列号管理分页
export const getSnPage = async (params) => {
  return await request.get({ url: `/system/sn/page`, params })
}

// 查询序列号管理详情
export const getSn = async (id: number) => {
  return await request.get({ url: `/system/sn/get?id=` + id })
}

// 新增序列号管理
export const createSn = async (data: SnVO) => {
  return await request.post({ url: `/system/sn/create`, data })
}

// 修改序列号管理
export const updateSn = async (data: SnVO) => {
  return await request.put({ url: `/system/sn/update`, data })
}

// 删除序列号管理
export const deleteSn = async (id: number) => {
  return await request.delete({ url: `/system/sn/delete?id=` + id })
}

// 导出序列号管理 Excel
export const exportSn = async (params) => {
  return await request.download({ url: `/system/sn/export-excel`, params })
}
