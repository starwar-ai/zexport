import request from '@/config/axios'

// 查询外销合同信息分页
export const getFactoryContractPage = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/page`, params })
}

// 查询外销合同信息详情
export const getFactoryContract = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/detail`, params })
}

//审核模块的获取详情
export const getAuditFactoryContract = async (params) => {
  return await request.get({ url: '/sms/factory/sale-contract/audit-detail', params })
}

// 新增外销合同信息
export const createFactoryContract = async (data) => {
  return await request.post({ url: `/sms/factory/sale-contract/create`, data })
}

// 修改外销合同信息
export const updateFactoryContract = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/update`, data })
}

// 删除外销合同信息
export const deleteFactoryContract = async (id: number) => {
  return await request.delete({ url: `/sms/factory/sale-contract/delete?id=` + id })
}

// 外销合同提交
export const submitFactoryContract = async (id) => {
  return await request.put({ url: `/sms/factory/sale-contract/submit?contactId=${id}` })
}

// 导出Excel
export const exportFactoryContract = async (params) => {
  return await request.download({ url: `/sms/factory/sale-contract/export-excel`, params })
}

// 查询分页
export const getFactoryContractChangePage = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/change-page`, params })
}
// 下推采购计划
export const toPurchasePlan = async (params) => {
  return await request.post({ url: `/sms/factory/sale-contract/to-purchase-plan`, params })
}
// 回签
export const signBack = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/sign-back`, data })
}

// 作废
export const close = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/close`, data })
}

// 批量查询产品库存
export const queryTotalStock = async (data) => {
  return await request.post({ url: `/wms/stock/queryTotalStock`, data })
}

//查询批次库存
export const listBatch = async (data) => {
  return await request.post({ url: `/wms/stock/listBatch`, data, neverLoading: true })
}
// 锁定库存-批量
export const createBatch = async (data) => {
  return await request.post({ url: `/wms/stock-lock/createBatch`, data })
}
// 释放库存-批量
export const cancelBatch = async (code) => {
  return await request.get({ url: `/wms/stock-lock/cancelBatch?saleContractCode=${code}` })
}
//根据code查询产品详情

export const getSkuInfo = async (code) => {
  return await request.get({ url: `pms/sku/get-simple-detail?skuCode=${code}` })
}

export const outNoticeMid = async (ids) => {
  return await request.get({ url: `/sms/factory/sale-contract/out-notice-mid?ids=${ids}` })
}

export const transformNotice = async (data) => {
  return await request.post({ url: `/sms/factory/sale-contract/transform-notice`, data })
}

//变更
export const getChangePage = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/change-page`, params })
}
export const createChange = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/change`, data })
}
export const getChangeInfo = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/change-detail`, params })
}
export const getChangeAuditInfo = async (params) => {
  return await request.get({ url: `/sms/factory/sale-contract/audit-change-detail`, params })
}

export const changeApproveApi = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/change-approve`, data })
}
export const changeRejectApi = async (data) => {
  return await request.put({ url: `/sms/factory/sale-contract/change-reject`, data })
}
