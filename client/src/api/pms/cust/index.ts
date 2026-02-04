import request from '@/config/axios'

// 查询分页
export const getCustSkuPage = async (params) => {
  return await request.get({ url: `/pms/csku/page`, params })
}
export const getCustSkuInfo = async (params) => {
  return await request.get({ url: `/pms/csku/detail`, params })
}
export const getCustSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/csku/audit-detail`, params })
}
// 删除
export const deleteCustSku = async (id: number) => {
  return await request.delete({ url: `/pms/csku/delete?id=` + id })
}
// 导出Excel
export const exportCustSku = async (params) => {
  return await request.download({ url: `/pms/csku/export-excel`, params })
}

export const cskuCreate = async (data) => {
  return await request.post({ url: `/pms/csku/create`, data })
}

export const cskuUpdate = async (data) => {
  return await request.put({ url: `/pms/csku/update`, data })
}

//提交
export const submitCsku = async (id) => {
  return await request.put({ url: `/pms/csku/submit?id=${id}` })
}

//通过
export const approveCsku = async (data) => {
  return await request.put({ url: `/pms/csku/approve`, data })
}
//不通过
export const rejectCsku = async (data) => {
  return await request.put({ url: `/pms/csku/reject`, data })
}

//
//变更提交
export const submitCskuChange = async (id) => {
  return await request.put({ url: `/pms/csku/change-submit?skuId=${id}` })
}

//变更通过
export const approveCskuChange = async (data) => {
  return await request.put({ url: `/pms/csku/change-approve`, data })
}
//变更不通过
export const rejectCskuChange = async (data) => {
  return await request.put({ url: `/pms/csku/change-reject`, data })
}

// 查询分页
export const getCustSkuChangePage = async (params) => {
  return await request.get({ url: `/pms/csku/change-page`, params })
}

// 变更产品资料
export const cskuChange = async (data) => {
  return await request.put({ url: `/pms/csku/change`, data })
}

export const cskuChangeDetail = async (params) => {
  return await request.get({ url: `/pms/csku/change-detail`, params })
}

export const cskuAuditChangeDetail = async (params) => {
  return await request.get({ url: `/pms/csku/audit-detail`, params })
}

export const setOnshelfFlag = async (params) => {
  return await request.put({ url: `/pms/csku/set-onshelf-flag`, params })
}

export const setAdvantage = async (params) => {
  return await request.put({ url: `/pms/base-sku/set-advantage`, params })
}
