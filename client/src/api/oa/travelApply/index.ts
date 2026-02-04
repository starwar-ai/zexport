import request from '@/config/axios'

// 查询分页
export const getTravelApplyPage = async (params) => {
  return await request.get({ url: `/oa/travel-apply/page`, params })
}
//新增
export const createTravelApply = async (data) => {
  return await request.post({ url: `/oa/travel-apply/create`, data })
}

export const getTravelApplyAuditInfo = async (params) => {
  return await request.get({ url: `/oa/travel-apply/audit-detail`, params })
}

export const getTravelApplyInfo = async (params) => {
  return await request.get({ url: `/oa/travel-apply/detail`, params })
}

// 修改
export const updateTravelApply = async (data) => {
  return await request.put({ url: `oa/travel-apply/update`, data })
}

// 删除
export const deleteTravelApply = async (id: number) => {
  return await request.delete({ url: `oa/travel-apply/delete?id=` + id })
}

// 导出Excel
export const exportTravelApply = async (params) => {
  return await request.download({ url: `oa/travel-apply/export-excel`, params })
}

//提交
export const submitTravelApply = async (id) => {
  return await request.put({ url: `oa/travel-apply/submit?entertainReimbId=${id}` })
}

//通过
export const approveTravelApply = async (data) => {
  return await request.put({ url: `oa/travel-apply/approve`, data })
}
//不通过
export const rejectTravelApply = async (data) => {
  return await request.put({ url: `oa/travel-apply/reject`, data })
}

//作废
export const closeTravelApply = async (id) => {
  return await request.put({ url: `oa/travel-apply/close?id=${id}` })
}
