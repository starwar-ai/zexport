import request from '@/config/axios'

// 查询分页
export const getSkuPage = async (params) => {
  return await request.get({ url: `/pms/base-sku/page`, params })
}
export const simpleSkuList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-sku`, params })
}
export const ownSkuList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-own-sku`, params })
}

//新增
export const createSku = async (data) => {
  return await request.post({ url: `/pms/base-sku/create`, data })
}

export const getSkuInfo = async (params) => {
  return await request.get({ url: `/pms/base-sku/detail`, params })
}
export const getSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/base-sku/audit-detail`, params })
}
// 修改
export const updateSku = async (data) => {
  return await request.put({ url: `/pms/base-sku/update`, data })
}

// 删除
export const deleteSku = async (id: number) => {
  return await request.delete({ url: `/pms/base-sku/delete?id=` + id })
}
//管理员删除
export const manageDel = async (id: number) => {
  return await request.delete({ url: `/pms/base-sku/manager-delete?id=` + id })
}

// 导出Excel
export const exportSku = async (params) => {
  return await request.download({ url: `/pms/base-sku/export-excel`, params })
}

//提交
export const submitSku = async (id) => {
  return await request.put({ url: `/pms/base-sku/submit?id=${id}` })
}

//通过
export const approveSku = async (data) => {
  return await request.put({ url: `/pms/base-sku/approve`, data })
}
//不通过
export const rejectSku = async (data) => {
  return await request.put({ url: `/pms/base-sku/reject`, data })
}

export const setAdvantage = async (params) => {
  return await request.put({ url: `/pms/base-sku/set-advantage`, params })
}
export const setPrice = async (id, data) => {
  return await request.put({ url: `/pms/base-sku/pricing?id=${id}`, data })
}

export const setOnshelfFlag = async (params) => {
  return await request.put({ url: `/pms/base-sku/set-onshelf-flag`, params })
}
// 成交历史
export const historyTradePrice = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/history-trade-price`, params })
}

// 变更产品资料
export const skuChange = async (data) => {
  return await request.put({ url: `/pms/base-sku/change`, data })
}

// 获得变更产品资料分页
export const skuChangePage = async (params) => {
  return await request.get({ url: `/pms/base-sku/change-page`, params })
}

// 获得变更产品详情
export const getChangeSkuInfo = async (params) => {
  return await request.get({ url: `/pms/base-sku/change-detail`, params })
}

export const getChangeSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/base-sku/audit-change-detail`, params })
}

// 更新变更产品资料
export const skuChangeUpdate = async (data) => {
  return await request.put({ url: `/pms/base-sku/change-update`, data })
}

//变更产品资料提交
export const submitSkuChange = async (id) => {
  return await request.put({ url: `/pms/base-sku/change-submit?skuId=${id}` })
}

//变更产品资料通过
export const approveSkuChange = async (data) => {
  return await request.put({ url: `/pms/base-sku/change-approve`, data })
}
//变更产品资料不通过
export const rejectSkuChange = async (data) => {
  return await request.put({ url: `/pms/base-sku/change-reject`, data })
}

// 获得变更影响列表
export const createSkuEffect = async (data) => {
  return await request.post({ url: `/pms/sku/change-effect`, data })
}
// 包装方式列表
export const packageTypeList = async (params) => {
  return await request.get({ url: `/pms/package-type/get-simple-list`, params })
}
//生成产品编号
export const generateCode = async (id) => {
  return await request.get({ url: `/pms/sku/generate-code?categoryId=${id}` })
}
//根据编号查询hscode
export const getHscodeByCode = async (code) => {
  return await request.get({ url: `/pms/hsdata/get-hsdata-by-code?code=${code}` })
}
//反审核
export const revertAudit = async (id) => {
  return await request.put({ url: `/pms/base-sku/anti-audit?skuId=${id}` })
}
