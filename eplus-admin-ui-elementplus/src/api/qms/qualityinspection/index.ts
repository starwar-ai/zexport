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
export const getQualityInspectionPage = async (params) => {
  return await request.get({ url: '/qms/quality-inspection/page', params })
}

// 查询验货单详情
export const getQualityInspection = async (id: number) => {
  return await request.get({ url: '/qms/quality-inspection/get?id=' + id })
}
// 获得验货单详情
export const getQualityInspectionDetail = async (id: number) => {
  return await request.get({ url: '/qms/quality-inspection/detail?id=' + id })
}

// 新增验货单
export const createQualityInspection = async (data: QualityInspectionVO) => {
  return await request.post({ url: '/qms/quality-inspection/create', data })
}

// 修改验货单
export const updateQualityInspection = async (data) => {
  return await request.put({ url: '/qms/quality-inspection/update', data })
}
export const closeQualityInspection = async (id) => {
  return await request.put({ url: `/qms/quality-inspection/close?id=${id}` })
}

// 删除验货单
export const deleteQualityInspection = async (id: number) => {
  return await request.delete({ url: '/qms/quality-inspection/delete?id=' + id })
}

// 导出验货单 Excel
export const exportQualityInspectionApi = async (params) => {
  return await request.download({ url: '/qms/quality-inspection/export-excel', params })
}

// 提交验货单
export const submitQualityInspectionApi = async (params) => {
  return await request.put({ url: '/qms/quality-inspection/submit', params })
}

// 验货
export const qmsUploadInspection = async (data) => {
  return await request.put({ url: '/qms/quality-inspection/uploadInspection', data })
}

// 确认排验
export const qmsVerification = async (data) => {
  return await request.put({ url: '/qms/quality-inspection/verification', data })
}

// 返工重验
export const qmsRework = async (data) => {
  return await request.put({ url: '/qms/quality-inspection/rework', data })
}

// 让步放行
export const qmsRelease = async (data) => {
  return await request.post({ url: '/scm/concession-release/create', data })
}

// 修改验货金额
export const updateAmount = async (data) => {
  return await request.put({ url: '/qms/quality-inspection/update-amount', data })
}

//导出
export const exportWord = async (params) => {
  return await request.download({
    url: `/qms/quality-inspection/export-word`,
    params,
    neverLoading: true
  })
}

export const exportExcel = async (params) => {
  return await request.download({
    url: `/qms/quality-inspection/export-excel-detail`,
    params,
    neverLoading: true
  })
}
