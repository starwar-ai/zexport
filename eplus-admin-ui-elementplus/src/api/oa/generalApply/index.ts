import request from '@/config/axios'

// 查询分页
export const getGeneralApplyPage = async (params) => {
  return await request.get({ url: `/oa/general-apply/page`, params })
}
//新增
export const createGeneralApply = async (data) => {
  return await request.post({ url: `/oa/general-apply/create`, data })
}

export const getGeneralApplyInfo = async (params) => {
  return await request.get({ url: `/oa/general-apply/detail`, params })
}

export const getGeneralApplyAuditInfo = async (params) => {
  return await request.get({ url: `/oa/general-apply/audit-detail`, params })
}

// 修改
export const updateGeneralApply = async (data) => {
  return await request.put({ url: `oa/general-apply/update`, data })
}

// 删除
export const deleteGeneralApply = async (id: number) => {
  return await request.delete({ url: `oa/general-apply/delete?id=` + id })
}

// 导出Excel
export const exportGeneralApply = async (params) => {
  return await request.download({ url: `oa/general-apply/export-excel`, params })
}

//提交
export const submitGeneralApply = async (id) => {
  return await request.put({ url: `oa/general-apply/submit?generalReimbId=${id}` })
}

//通过
export const approveGeneralApply = async (data) => {
  return await request.put({ url: `oa/general-apply/approve`, data })
}
//不通过
export const rejectGeneralApply = async (data) => {
  return await request.put({ url: `oa/general-apply/reject`, data })
}

//作废
export const closeGeneralApply = async (id) => {
  return await request.put({ url: `oa/general-apply/close?id=${id}` })
}
