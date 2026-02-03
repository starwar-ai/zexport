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

// 查询寄件列表
export const getEmsPage = async (params) => {
  return await request.get({ url: '/ems/send/page', params })
}
// 获得寄件详情
export const getEmSDetail = async (id: number | string) => {
  return await request.get({ url: '/ems/send/detail?id=' + id })
}
export const getEmSAuditDetail = async (id: number | string) => {
  return await request.get({ url: '/ems/send/audit_detail?id=' + id })
}
// 删除寄件单
export const deleteEms = async (id: number) => {
  return await request.delete({ url: '/ems/send/delete?id=' + id })
}

// 修改寄件单
export const updateEms = async (data) => {
  return await request.put({ url: '/ems/send/update', data })
}

// 编辑提交
export const submitSend = async (id: number) => {
  return await request.put({ url: '/ems/send/submit?id=' + id })
}

// 提交寄件单
export const createSubmitSend = async (data) => {
  return await request.post({ url: '/ems/send/create-submit', data })
}

// 新增寄件单
export const createSend = async (data) => {
  return await request.post({ url: '/ems/send/create', data })
}

// 上传快递单号
export const uploadNumber = async (params) => {
  return await request.post({ url: '/ems/send/upload-number', params })
}

// // 导入execl
export const sendBillImport = async (data) => {
  return await request.post({ url: '/ems/send-bill/import', data })
}

// 导出
export const exportExcel = async (params) => {
  return await request.download({ url: '/ems/send/export-excel', params })
}

// 回填费用-批量
export const backfillImport = async (data) => {
  return await request.post({ url: '/ems/send/import', data })
}

// 回填费用-单个
export const updateCost = async (data) => {
  return await request.post({ url: '/ems/send/update-cost', data })
}

// 删除
export const deleteEmsSend = async (id: number) => {
  return await request.delete({ url: '/ems/send/delete?id=' + id })
}

// 获得快递公司精简列表
export const getSimpleList = async () => {
  return await request.get({
    url: '/scm/vender/get-simple-list',
    params: { pageSize: 100, pageNo: 1, administrationVenderType: 1 }
  })
}

// 通过审核
export const examineApprove = async (data) => {
  return await request.put({ url: '/ems/send/approve', data: data })
}
// 审核不通过
export const examineReject = async (data) => {
  return await request.put({ url: '/ems/send/reject', data: data })
}

// 更新费用归集
export const updateFeeShare = async (data) => {
  return await request.put({ url: '/ems/send/fee-share-update', data })
}

export const purchaseList = async (params) => {
  return await request.get({ url: '/scm/purchase-contract/get-simple-list', params })
}

export const contractList = async (params) => {
  return await request.get({ url: '/sms/export/sale-contract/get-simple-list', params })
}

//批量上传图片
export const uploadImgs = (data) => {
  return request.upload({ url: '/infra/file/batch-upload', data: data })
}

// 确认导入
export const confirmImportSend = async (params) => {
  return await request.post({ url: `/ems/send/import`, params })
}

export const closeSend = async (id: number) => {
  return await request.put({ url: `/ems/send/close?id=${id}` })
}

// 修改快递公司
export const updateVender = async (params) => {
  return await request.put({ url: '/ems/send/update-vender', params })
}
