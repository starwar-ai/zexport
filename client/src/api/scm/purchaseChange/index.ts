import request from '@/config/axios'

// 查询分页
export const getChangePage = async (params) => {
  return await request.get({ url: `scm/purchase-contract/change-page`, params })
}
//新增
export const createChange = async (data) => {
  return await request.put({ url: `scm/purchase-contract/change`, data })
}
export const updateChange = async (data) => {
  return await request.put({ url: `scm/purchase-contract/change-update`, data })
}

export const getChangeInfo = async (params) => {
  return await request.get({ url: `scm/purchase-contract/change-detail`, params })
}
export const getChangeAuditInfo = async (params) => {
  return await request.get({ url: `scm/purchase-contract/audit-change-detail`, params })
}
// 删除
export const deleteChange = async (id: number) => {
  return await request.delete({ url: `scm/purchase-contract/change-delete?id=` + id })
}

//提交
export const submitChange = async (id) => {
  return await request.put({ url: `scm/purchase-contract/change-submit?id=${id}` })
}

//通过
export const approveChange = async (data) => {
  return await request.put({ url: `scm/purchase-contract/change-approve`, data })
}
//不通过
export const rejectChange = async (data) => {
  return await request.put({ url: `scm/purchase-contract/change-reject`, data })
}

export const portList = async (params) => {
  return await request.get({ url: `system/port/get-simple-list`, params })
}
