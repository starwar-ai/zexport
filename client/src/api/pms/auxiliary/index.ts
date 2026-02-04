import request from '@/config/axios'

// 查询分页
export const getSkuPage = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/page`, params })
}
export const simpleSkuList = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/simple-sku`, params })
}

//新增
export const createSku = async (data) => {
  return await request.post({ url: `/pms/auxiliary/sku/create`, data })
}

export const getSkuInfo = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/detail`, params })
}
export const getSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/audit-detail`, params })
}
// 修改
export const updateSku = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/update`, data })
}

// 删除
export const deleteSku = async (id: number) => {
  return await request.delete({ url: `/pms/auxiliary/sku/delete?id=` + id })
}

// 导出Excel
export const exportSku = async (params) => {
  return await request.download({ url: `/pms/auxiliary/sku/export-excel`, params })
}

//提交
export const submitSku = async (id) => {
  return await request.put({ url: `/pms/auxiliary/sku/submit?id=${id}` })
}

//通过
export const approveSku = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/approve`, data })
}
//不通过
export const rejectSku = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/reject`, data })
}

export const setAdvantage = async (params) => {
  return await request.put({ url: `/pms/auxiliary/sku/set-advantage`, params })
}
export const setPrice = async (id, data) => {
  return await request.put({ url: `/pms/auxiliary/sku/pricing?id=${id}`, data })
}

export const setOnshelfFlag = async (params) => {
  return await request.put({ url: `/pms/auxiliary/sku/set-onshelf-flag`, params })
}
//获得辅料采购记录分页
export const getPurchaseInfo = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/purchase-page`, params })
}

// 变更
export const auxiliaryChange = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/change`, data })
}

// 变更查询分页
export const getSkuChangePage = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/change-page`, params })
}

// 变更详情
export const getSkuChangeInfo = async (params) => {
  return await request.get({ url: `/pms/auxiliary/sku/change-detail`, params })
}

// 变更修改
export const updateChangeSku = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/change-update`, data })
}

// 变更删除
export const deleteChangeSku = async (id: number) => {
  return await request.delete({ url: `/pms/auxiliary/sku/change-delete?id=` + id })
}

// 变更提交
export const submitSkuChange = async (skuId) => {
  return await request.put({ url: `/pms/auxiliary/sku/change-submit?skuId=${skuId}` })
}

//变更通过
export const approveSkuChange = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/change-approve`, data })
}
//变更不通过
export const rejectSkuChange = async (data) => {
  return await request.put({ url: `/pms/auxiliary/sku/change-reject`, data })
}
