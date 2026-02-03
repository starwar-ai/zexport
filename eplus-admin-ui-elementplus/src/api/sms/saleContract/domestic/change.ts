import request from '@/config/axios'

// 查询分页
export const getChangePage = async (params) => {
  return await request.get({ url: `sms/domestic/sale-contract/change-page`, params })
}
//新增
export const createChange = async (data) => {
  return await request.put({ url: `sms/domestic/sale-contract/change`, data })
}
export const updateChange = async (data) => {
  return await request.put({ url: `sms/domestic/sale-contract/change-update`, data })
}

export const getChangeInfo = async (params) => {
  return await request.get({ url: `sms/domestic/sale-contract/change-detail`, params })
}
export const getChangeAuditInfo = async (params) => {
  return await request.get({ url: `sms/domestic/sale-contract/audit-change-detail`, params })
}
// 删除
export const deleteChange = async (id: number) => {
  return await request.delete({ url: `sms/domestic/sale-contract/change-delete?id=` + id })
}

//提交
export const submitChange = async (id) => {
  return await request.put({ url: `sms/domestic/sale-contract/change-submit?id=${id}` })
}

//通过
export const approveChange = async (data) => {
  return await request.put({ url: `sms/domestic/sale-contract/change-approve`, data })
}
//不通过
export const rejectChange = async (data) => {
  return await request.put({ url: `sms/domestic/sale-contract/change-reject`, data })
}
