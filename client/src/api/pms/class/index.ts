import request from '@/config/axios'

// 查询分页
export const getClassPage = async (params) => {
  return await request.get({ url: `/pms/category/page`, params })
}
//新增
export const createClass = async (data) => {
  return await request.post({ url: `/pms/category/create`, data })
}

export const getClassInfo = async (id) => {
  return await request.get({ url: `/pms/category/get?id=${id}` })
}

export const getClassTree = async () => {
  return await request.get({ url: `/pms/category/get-simple-list` })
}

// 修改
export const updateClass = async (data) => {
  return await request.put({ url: `/pms/category/update`, data })
}

// 删除
export const deleteClass = async (id: number) => {
  return await request.delete({ url: `/pms/category/delete?id=` + id })
}

// 导出Excel
export const exportClass = async (params) => {
  return await request.download({ url: `/pms/category/export-excel`, params })
}
//获取海关编码
export const getHsData = async (params) => {
  return await request.get({ url: `/pms/hsdata/page`, params })
}
