import request from '@/config/axios'

export interface QualityInspectionVO {
  id: number
  code: string
  auditStatus: number
  qualityInspectionStatus: number
  reinspectionFlag: number
  sourceId: number
  reworkFlag: number
  reworkPicture: string
  reworkDesc: string
  sourceType: number
  inspectionType: number
  venderId: number
  venderCode: string
  expectInspectionTime: Date
  applyInspectorId: number
  applyInspectorName: string
  applyInspectorDeptId: number
  applyInspectorDeptName: string
  planInspectionTime: Date
  inspectorId: number
  inspectorName: string
  inspectorDeptId: number
  inspectorDeptName: string
  inspectionTime: Date
  specialAttentionNotice: string
  amount: string
  annex: string
  guaranteeLetter: string
  acceptDesc: string
  remark: string
}

// 查询验货单列表
export const getDesignTaskPage = async (params) => {
  return await request.get({ url: '/dtms/design/page', params })
}
// 查询验货单列表
export const getDesignTaskClaimPage = async (params) => {
  return await request.get({ url: '/dtms/design/claimPage', params })
}

export const getDesignTaskAuditDetail = async (id) => {
  return await request.get({ url: `/dtms/design/audit-detail?id=${id}` })
}

// 新增设计任务
export const createDesignTask = async (data: QualityInspectionVO) => {
  return await request.post({ url: '/dtms/design/create', data })
}

// 提交验货单
export const submitDesignApi = async (params) => {
  return await request.put({ url: '/dtms/design/submit', params })
}

// 设计任务认领
export const createDesignTaskClaim = async (data: QualityInspectionVO) => {
  return await request.post({ url: '/dtms/design-item/create', data })
}

// 获取任务进度
export const getDesignSummary = async (params) => {
  return await request.get({ url: '/dtms/design-summary/page', params })
}
// 总结详情
export const getDesignSummaryDetail = async (data) => {
  return await request.post({ url: '/dtms/design-summary/detail', data })
}
// 提交任务进度

export const getDesignSummaryCreate = async (data) => {
  return await request.post({ url: '/dtms/design-summary/create', data })
}

// 完成设计
export const completeDesign = async (data) => {
  return await request.put({ url: '/dtms/design-item/complate', data })
}

// 认领明细

export const designItemPage = async (params) => {
  return await request.get({ url: '/dtms/design-item/page', params })
}

// 评价
export const evaluateDesign = async (data) => {
  return await request.put({ url: '/dtms/design-item/evaluate', data })
}

// 更新
export const designUpdate = async (data) => {
  return await request.put({ url: '/dtms/design/update', data })
}

// 详情
export const designDetail = async (id: number) => {
  return await request.get({ url: '/dtms/design/detail?id=' + id })
}

// 删除
export const deleteDesign = async (id: number) => {
  return await request.delete({ url: '/dtms/design/delete?id=' + id })
}

// 删除
export const cancleClaim = async (id: number) => {
  return await request.delete({ url: '/dtms/design/cancleClaim?id=' + id })
}
