import request from '@/config/axios'

// 查询分页
export const getOwnSkuPage = async (params) => {
  return await request.get({ url: `/pms/own-brand/page`, params })
}
// 导出Excel
export const exportOwnSku = async (params) => {
  return await request.download({
    url: `/pms/own-brand/export-excel`,
    params
  })
}

export const getOwnSkuInfo = async (params) => {
  return await request.get({ url: `/pms/own-brand/detail`, params })
}
export const getOwnSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/own-brand/audit-detail`, params })
}

// 删除
export const delOwnSku = async (id: number) => {
  return await request.delete({ url: `/pms/own-brand/delete?id=` + id })
}

export const ownSkuUpdate = async (data) => {
  return await request.put({ url: `/pms/own-brand/update`, data })
}

export const ownSkuCreate = async (data) => {
  return await request.post({ url: `/pms/own-brand/create`, data })
}

//提交
export const submitOsku = async (id) => {
  return await request.put({ url: `/pms/own-brand/submit?id=${id}` })
}

//通过
export const approveOsku = async (data) => {
  return await request.put({ url: `/pms/own-brand/approve`, data })
}
//不通过
export const rejectOsku = async (data) => {
  return await request.put({ url: `/pms/own-brand/reject`, data })
}

//变更审核通过
export const changeApproveOsku = async (data) => {
  return await request.put({ url: `/pms/own-brand/change-approve`, data })
}
//变更审核不通过
export const changeRejectOsku = async (data) => {
  return await request.put({ url: `/pms/own-brand/change-reject`, data })
}

export const ownSkuChange = async (data) => {
  return await request.put({ url: `/pms/own-brand/change`, data })
}

// 变更列表

export const getOwnSkuChangePage = async (params) => {
  return await request.get({ url: `/pms/own-brand/change-page`, params })
}

// 变更列表详情
export const getOwnSkuChangeDetail = async (params) => {
  return await request.get({ url: `/pms/own-brand/change-detail`, params })
}

// 修改
export const updateOwnSku = async (data) => {
  return await request.put({ url: `/pms/own-brand/change-update`, data })
}

// 删除
export const deleteOwnSku = async (id: number) => {
  return await request.delete({ url: `/pms/own-brand/change-delete?id=` + id })
}

//变更产品资料提交
export const submitOwnSkuChange = async (id) => {
  return await request.put({ url: `/pms/own-brand/change-submit?skuId=${id}` })
}

export const setOnshelfFlag = async (params) => {
  return await request.put({ url: `/pms/own-brand/set-onshelf-flag`, params })
}

export const setAdvantage = async (params) => {
  return await request.put({ url: `/pms/own-brand/set-advantage`, params })
}
