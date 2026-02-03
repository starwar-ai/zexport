import request from '@/config/axios'

// 查询积木报表
export const getJMReportList = async () => {
  return await request.get({ url: `/dpms/report/list` })
}

// 新增报表
export const createReport = async (data) => {
  return await request.post({ url: `/dpms/report/create`, data })
}

// 获取全部报表配置
export const getAllList = async (data) => {
  return await request.post({ url: `/dpms/report/get-all-list`, data })
}

// 赋予角色报表
export const assignRoleCard = async (data) => {
  return await request.post({ url: `/dpms/report-role/assign-role-card`, data })
}

// 批量创建报表配置
export const batchCreate = async (data) => {
  return await request.post({ url: `/dpms/report/batch-create `, data })
}

// 获得角色报表配置
export const getJMReportRoleList = async () => {
  return await request.get({ url: `/dpms/report/role-list` })
}

// 查询Ureport2报表详情
export const getUReportData = async (id: number) => {
  return await request.get({ url: `/dpms/report/create` })
}

// 删除报表
export const deleteReportData = async (id: number) => {
  return await request.delete({ url: `dpms/report/delete?id=` + id })
}
