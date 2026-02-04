import request from '@/config/axios'

export interface QualityInspectionItemVO {
  id: number
  inspectionId: number
  skuId: number
  skuCode: string
  skuName: string
  cskuCode: string
  inspectionStatus: number
  failDesc: string
  pendingDesc: string
  quantity: number
  purchaseContractCode: string
  purchaseUserId: number
  purchaseUserName: string
  purchaseUserDeptId: number
  purchaseUserDeptName: string
  saleContractCode: string
  saleUserId: number
  saleUserName: string
  saleUserDeptId: number
  saleUserDeptName: string
  trackUserId: number
  trackUserName: string
  trackUserDeptId: number
  trackUserDeptName: string
  custId: number
  custCode: string
  custName: string
  qtyPerOuterbox: number
  qtyPerInnerbox: number
  boxCount: number
  remark: string
}

// 查询验货单-明细列表
export const getQualityInspectionItemPage = async (params) => {
  return await request.get({ url: '/qms/quality-inspection-item/page', params })
}

// 查询验货单-明细详情
export const getQualityInspectionItem = async (id: number) => {
  return await request.get({ url: '/qms/quality-inspection-item/get?id=' + id })
}

// 新增验货单-明细
export const createQualityInspectionItem = async (data: QualityInspectionItemVO) => {
  return await request.post({ url: '/qms/quality-inspection-item/create', data })
}

// 修改验货单-明细
export const updateQualityInspectionItem = async (data: QualityInspectionItemVO) => {
  return await request.put({ url: '/qms/quality-inspection-item/update', data })
}

// 删除验货单-明细
export const deleteQualityInspectionItem = async (id: number) => {
  return await request.delete({ url: '/qms/quality-inspection-item/delete?id=' + id })
}

// 导出验货单-明细 Excel
export const exportQualityInspectionItemApi = async (params) => {
  return await request.download({ url: '/qms/quality-inspection-item/export-excel', params })
}
