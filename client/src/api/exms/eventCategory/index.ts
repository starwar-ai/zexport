import request from '@/config/axios'

// 查询分页
export const getEventCategoryPage = async (params) => {
  return await request.get({ url: `/exms/event-category/page`, params })
}

export const getEventCategoryInfo = async (id) => {
  return await request.get({ url: `/exms/event-category/detail?id=` + id })
}

//新增
export const createEventCategory = async (data) => {
  return await request.post({ url: `/exms/event-category/create`, data })
}

// 修改
export const updateEventCategory = async (data) => {
  return await request.put({ url: `/exms/event-category/update`, data })
}

// 删除
export const deleteEventCategory = async (id: number) => {
  return await request.delete({ url: `/exms/event-category/delete?id=` + id })
}

//获得展会系列列表
export const getEventCategory = async (params) => {
  return await request.get({ url: `/exms/event-category/get-simple-list`, params })
}
