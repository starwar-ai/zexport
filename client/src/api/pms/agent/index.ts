import request from '@/config/axios'

// 查询分页
export const getAgentSkuPage = async (params) => {
  return await request.get({ url: `/pms/agent-sku/page`, params })
}

//新增
export const createAgentSku = async (data) => {
  return await request.post({ url: `/pms/agent-sku/create`, data })
}
// 修改
export const updateAgentSku = async (data) => {
  return await request.put({ url: `/pms/agent-sku/update`, data })
}

// 删除
export const deleteAgentSku = async (id: number) => {
  return await request.delete({ url: `/pms/agent-sku/delete?id=` + id })
}

//反审核
export const revertAudit = async (id) => {
  return await request.put({ url: `/pms/agent-sku/anti-audit?skuId=${id}` })
}

export const getSkuInfo = async (params) => {
  return await request.get({ url: `/pms/agent-sku/detail`, params })
}
export const getSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/agent-sku/audit-detail`, params })
}

// 导出Excel
export const exportSku = async (params) => {
  return await request.download({ url: `/pms/agent-sku/export-excel`, params })
}

//提交
export const submitSku = async (id) => {
  return await request.put({ url: `/pms/agent-sku/submit?id=${id}` })
}

//通过
export const approveApi = async (data) => {
  return await request.put({ url: `/pms/agent-sku/approve`, data })
}
//不通过
export const rejectApi = async (data) => {
  return await request.put({ url: `/pms/agent-sku/reject`, data })
}

// 成交历史
export const historyTradePrice = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/history-trade-price`, params })
}

// 变更产品资料
export const agentSkuChange = async (data) => {
  return await request.put({ url: `/pms/agent-sku/change`, data })
}

// 获得变更产品资料分页
export const agentSkuChangePage = async (params) => {
  return await request.get({ url: `/pms/agent-sku/change-page`, params })
}

// 获得变更产品详情
export const getChangeAgentSkuInfo = async (params) => {
  return await request.get({ url: `/pms/agent-sku/change-detail`, params })
}

export const getChangeAgentSkuAuditInfo = async (params) => {
  return await request.get({ url: `/pms/agent-sku/audit-change-detail`, params })
}

// 更新变更产品资料
export const skuChangeUpdate = async (data) => {
  return await request.put({ url: `/pms/agent-sku/change-update`, data })
}

//变更产品资料提交
export const submitSkuChange = async (id) => {
  return await request.put({ url: `/pms/agent-sku/change-submit?skuId=${id}` })
}

//变更产品资料通过
export const approveSkuChange = async (data) => {
  return await request.put({ url: `/pms/agent-sku/change-approve`, data })
}
//变更产品资料不通过
export const rejectSkuChange = async (data) => {
  return await request.put({ url: `/pms/agent-sku/change-reject`, data })
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
