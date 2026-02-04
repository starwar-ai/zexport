import request from '@/config/axios'
// 查询分页
export const getShipmentPage = async (params) => {
  return await request.get({ url: `/dms/shipment/page`, params })
}
//新增
export const createShipment = async (data) => {
  return await request.post({ url: `/dms/shipment/create`, data })
}

export const getShipmentInfo = async (params) => {
  return await request.get({ url: `/dms/shipment/detail`, params })
}
// 修改
export const updateShipment = async (data) => {
  return await request.put({ url: `/dms/shipment/update`, data })
}

// 删除
export const deleteShipment = async (id: number) => {
  return await request.delete({ url: `/dms/shipment/delete?id=${id}` })
}

// 导出Excel
export const exportShipment = async (params) => {
  return await request.download({ url: `/dms/shipment/export-excel`, params })
}

export const exportShipmentDetail = async (params) => {
  return await request.download({ url: `/dms/shipment/export-detail-excel`, params })
}

export const forwarderSimpleList = async (params) => {
  return await request.get({ url: `/dms/forwarder-company-info/get-simple-list`, params })
}
//转商检单
export const toCommodityInspection = async (params) => {
  return await request.post({ url: `/dms/shipment/to-commodity-inspection`, params })
}

//转结汇单
export const toSettlementForm = async (data) => {
  return await request.post({ url: `/dms/shipment/to-settlement-form`, data })
}

//转报关单
export const toDeclaration = async (data) => {
  return await request.post({ url: `/dms/shipment/to-declaration`, data })
}
// 批量转
export const batchToDeclaration = async (data) => {
  return await request.post({ url: `/dms/shipment/batch-to-declaration`, data })
}

//作废
export const shipmentClose = async (data) => {
  return await request.put({ url: `/dms/shipment/close`, data })
}

//反作废
export const shipmentRollbackClose = async (data) => {
  return await request.put({ url: `/dms/shipment/rollback-close`, data })
}

//确认出运
export const handleShipment = async (data) => {
  return await request.put({ url: `/dms/shipment/shipment`, data })
}

//交单
export const shipmentFinish = async (params) => {
  return await request.get({ url: `/dms/shipment/finish`, params })
}
//转开票通知
export const toInvoicNotice = async (data) => {
  return await request.post({ url: `/dms/shipment/transform-invoicing-notices`, data })
}
//变更
export const changeShipment = async (data) => {
  return await request.put({ url: `/dms/shipment/change`, data })
}
//业务员变更
export const changeSalesman = async (data) => {
  return await request.put({ url: `/dms/shipment/change-business`, data })
}
//变更列表
export const getChangeList = async (params) => {
  return await request.get({ url: `/dms/shipment/change-page`, params })
}
//变更详情
export const getChangeDetail = async (params) => {
  return await request.get({ url: `/dms/shipment/change-detail`, params })
}
export const getAuditChangeDetail = async (params) => {
  return await request.get({ url: `/dms/shipment/change-audit-detail`, params })
}

// 确认
export const changeConfirm = async (id) => {
  return await request.put({ url: `/dms/shipment/confirm?id=${id}` })
}

//获取列表来源
export const getConfirmSource = async (params) => {
  return await request.get({ url: `/dms/shipment/confirm-source`, params })
}

//详情页面更新单证费用
export const forwarderFeeUpdate = async (data) => {
  return await request.put({ url: `/dms/shipment/forwarder-fee-update`, data })
}
//通过出运明细id获取拆分数据
export const getSplitList = async (ids) => {
  return await request.put({ url: `/dms/shipment/split-item?ids=${ids}` })
}
//获取加工列表
export const getListManufactureSku = async (params) => {
  return await request.get({ url: `/dms/shipment/listManufactureSku`, params })
}

//确认加工
export const manufactureSku = async (params) => {
  return await request.get({ url: `/dms/shipment/manufactureSku`, params })
}

//批量确认加工
export const batchManufactureSku = async (data) => {
  return await request.post({ url: `/dms/shipment/batchManufactureSku`, data })
}
//反审核
export const revertAudit = async (id) => {
  return await request.put({ url: `/scm/vender/anti-audit?venderId=${id}` })
}

// 变更通过审核
export const examineChangeApprove = async (data) => {
  return await request.put({ url: '/dms/shipment/change-approve', data: data })
}
// 变更审核不通过
export const examineChangeReject = async (data) => {
  return await request.put({ url: '/dms/shipment/change-reject', data: data })
}

export const genContract = async (id) => {
  return await request.put({ url: `/dms/shipment/gen-contract?shipmentId=${id}` })
}

export const genContractDel = async (id) => {
  return await request.put({ url: `/dms/shipment/delete-gen-contract?shipmentId=${id}` })
}

export const updateBatchFlag = async (id, type) => {
  return await request.put({ url: `/dms/shipment/${type}?shipmentId=${id}` })
}

export const splitShipment = async (data) => {
  return await request.put({ url: `/dms/shipment/split-shipment`, data })
}

export const deleteShipmentItem = async (params) => {
  return await request.put({ url: `/dms/shipment/delete-item`, params })
}

export const mergeShipmentItem = async (params) => {
  return await request.put({ url: `/dms/shipment/merge-item`, params })
}
