import request from '@/config/axios'
// 查询分页
export const getTransferOrderPage = async (params) => {
  return await request.get({ url: `/wms/transfer-order/page`, params })
}
//新增
export const createTransferOrder = async (data) => {
  return await request.post({ url: `/wms/transfer-order/create`, data })
}
export const getTransferOrderInfo = async (params) => {
  return await request.get({ url: `/wms/transfer-order/detail`, params })
}
// 修改
export const updateTransferOrder = async (data) => {
  return await request.put({ url: `/wms/transfer-order/update`, data })
}
// 删除
export const deleteTransferOrder = async (id: number) => {
  return await request.delete({ url: `/wms/transfer-order/delete?id=${id}` })
}
// 导出Excel
export const exportTransferOrder = async (params) => {
  return await request.download({ url: `/wms/transfer-order/export-excel`, params })
}
//提交
export const submitTransferOrder = async (id) => {
  return await request.put({ url: `/wms/transfer-order/submit`, id })
}
//获取可销售明细库存信息
export const getTransferOrderDetailStock = async (params) => {
  return await request.get({ url: `/sms/export/sale-contract/get-item-stock`, params })
}
