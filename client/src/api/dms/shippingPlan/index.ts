import request from '@/config/axios'
// 查询分页
export const getShipmentPlanPage = async (params) => {
  return await request.get({ url: `/dms/shipment-plan/page`, params })
}
//新增
export const createShipmentPlan = async (data) => {
  return await request.post({ url: `/dms/shipment-plan/create`, data })
}

export const getShipmentPlanInfo = async (params) => {
  return await request.get({ url: `/dms/shipment-plan/detail`, params })
}
export const getShipmentPlanUpdateInfo = async (params) => {
  return await request.get({ url: `/dms/shipment-plan/update-detail`, params })
}

// 修改
export const updateShipmentPlan = async (data) => {
  return await request.put({ url: `/dms/shipment-plan/update`, data })
}

// 删除
export const deleteShipmentPlan = async (id: number) => {
  return await request.delete({ url: `/dms/shipment-plan/delete?id=${id}` })
}

// 导出Excel
export const exportShipmentPlan = async (params) => {
  return await request.download({ url: `/dms/shipment-plan/export-excel`, params })
}
// 查询销售合同明细
export const getSaleContractList = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/push-out-shipment-plan`, params })
}

export const closeShipmentPlan = async (data) => {
  return await request.put({ url: `/dms/shipment-plan/close`, data })
}

//转出运明细/
export const transformShipment = async (params) => {
  return await request.post({ url: `/dms/shipment/transform-shipment`, params })
}

export const batchMerge = async (params) => {
  return await request.post({
    url: `/dms/shipment/batch-transform-shipment`,
    params
  })
}

export const handleAppend = async (data) => {
  return await request.post({
    url: `/dms/shipment/append-shipment`,
    data
  })
}

export const exportShipmentPlanDetail = async (params) => {
  return await request.download({ url: `/dms/shipment-plan/export-detail-excel`, params })
}
