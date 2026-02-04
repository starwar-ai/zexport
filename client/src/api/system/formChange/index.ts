import request from '@/config/axios'

export type FormChangeVO = {
  id: number
  path: string
  name: string
  processInstanceId: string
  instanceFlag: number
}

//获取表单变更管理分页
export const getFormChangePage = (params) => {
  return request.get({ url: '/system/form-change/page', params })
}
//表单变更管理详情
export const getFormChange = async (id) => {
  return await request.get({ url: `/system/form-change/detail?id=` + id })
}
// 新增表单变更管理
export const createFormChange = async (data: FormChangeVO) => {
  return await request.post({ url: `/system/form-change/create`, data })
}

// 修改表单变更管理
export const updateFormChange = async (data: FormChangeVO) => {
  return await request.put({ url: `/system/form-change/update`, data })
}

// 删除表单变更管理
export const deleteFormChange = async (id: number) => {
  return await request.delete({ url: `/system/form-change/delete?id=` + id })
}

//导出表单变更管理
export const exportFormChange = (params) => {
  return request.download({ url: '/system/form-change/export-excel', params })
}

//导出表单变更管理
export const getMenuList = (params) => {
  return request.get({ url: '/system/form-change/getMenuList', params })
}
