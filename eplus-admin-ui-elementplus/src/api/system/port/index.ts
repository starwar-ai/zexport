import request from '@/config/axios'

// 查询列表
export const getPortPage = async (params: PageParam) => {
  return await request.get({ url: '/system/port/page', params })
}

// 查询详情
export const getPort = async (id: number) => {
  return await request.get({ url: '/system/port/detail?id=' + id })
}

// 新增
export const createPort = async (data) => {
  return await request.post({ url: '/system/port/create', data })
}

// 修改
export const updatePort = async (data) => {
  return await request.put({ url: '/system/port/update', data })
}

// 删除
export const deletePort = async (id) => {
  return await request.delete({ url: '/system/port/delete?id=' + id })
}

// 导出
export const exportPort = async (params) => {
  return await request.download({ url: '/system/port/export', params })
}

//口岸分页
export const getPortList = async (params) => {
  return await request.get({ url: `/system/port/get-simple-list`, params })
}

export const portTop = async (type, params) => {
  return await request.post({ url: `/system/port/${type}`, params })
}
