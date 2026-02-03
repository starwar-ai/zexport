import request from '@/config/axios'

//新增
export const createContainer = async (data) => {
  return await request.post({ url: `/dms/shipment/transform-container-transportation`, data })
}

export const getMidContainer = async (params) => {
  return await request.get({ url: `/dms/shipment/container-transportation-mid-page`, params })
}
//获取分页
export const getContainerPage = async (params) => {
  return await request.get({ url: `/wms/stockNotice/container-transportation-page`, params })
}
//转结汇单
export const toSettlement = async (data) => {
  return await request.post({ url: `/dms/shipment/to-settlement-form`, data })
}
//转报关单
export const toDeclaration = async (data) => {
  return await request.post({ url: `/dms/shipment/to-declaration`, data })
}
//转商检单
export const toInspection = async (data) => {
  return await request.post({ url: `/dms/shipment/to-commodity-inspection`, data })
}
//作废
export const close = async (id) => {
  return await request.put({ url: `/wms/stockNotice/container-transportation/close?id=${id}` })
}
