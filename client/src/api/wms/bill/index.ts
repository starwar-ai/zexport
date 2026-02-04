import request from '@/config/axios'

export interface BillVO {
  id: number
  code: string
  billType: number
  auditStatus: number
  billStatus: number
  noticeCode: string
  purchaseContractId: number
  purchaseContractCode: string
  saleContractId: number
  saleContractCode: string
  billTime: Date
  purchaserId: number
  purchaserDeptId: number
  salesId: number
  salesDeptId: number
  warehouseId: number
  warehouseName: string
  printFlag: number
  printTimes: number
  companyId: number
  companyName: string
  remark: string
}

// 查询分页
export const getBillPage = async (params) => {
  return await request.get({ url: `/wms/bill/page`, params })
}

// 查询详情
export const getBill = async (params) => {
  return await request.get({ url: `/wms/bill/detail`, params })
}

// 新增
export const createBill = async (data: BillVO) => {
  return await request.post({ url: `/wms/bill/create`, data })
}

// 修改
export const updateBill = async (data: BillVO) => {
  return await request.put({ url: `/wms/bill/update`, data })
}

export const cancelBill = async (id) => {
  return await request.get({ url: `/wms/bill/cancel?id=${id}` })
}

// 删除
export const deleteBill = async (id: number) => {
  return await request.delete({ url: `/wms/bill/delete?id=` + id })
}

// 导出 Excel
export const exportBill = async (params) => {
  return await request.download({ url: `/wms/bill/export-excel`, params })
}

//供应商提交
export const submitBill = async (id) => {
  return await request.put({ url: `/wms/bill/submit?BillId=${id}` })
}
