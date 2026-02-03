import request from '@/config/axios'

export interface VenderVO {
  id: number
  ver: number
  code: string
  name: string
  nameEng: string
  nameShort: string
  provinceId: number
  cityId: number
  address: string
  licenseNo: string
  phone: string
  buyerId: number
  pocName: string
  pocTypes: string
  pocMobile: number
  abroadFlag: number
  countryId: number
  currency: string
  taxRate: number
  taxType: string
  paymentTermId: number
  auditStatus: string
  bank: string
  bankAccount: string
  creator: string
  creatorName: string
  createTime: Date
  fileList: []
  bankaccountSaveReqList: [
    {
      bank: undefined
      bankAccount: undefined
      defaultFlag: undefined
    }
  ]
  managerSaveReqList: [
    {
      manager_id: undefined
      defaultFlag: undefined
    }
  ]
  pocSaveReqList: [
    {
      name: undefined
      email: undefined
      mobile: undefined
      pocTypes: undefined
      address: undefined
      defaultFlag: undefined
      taskId: string
      createUser: string
      auditUser: string
      auditStatus: number
    }
  ]
  auditInfo: {
    taskId: string
    createUser: string
    auditUser: string
    auditStatus: number
  }
  submitFlag: number
}

// 查询供应商信息分页
export const getVenderPage = async (params) => {
  return await request.get({ url: `/scm/vender/page`, params })
}

export const getAdministrationVenderPage = async (params) => {
  return await request.get({ url: `/scm/vender/page-no-permission`, params })
}

// 查询供应商信息详情
export const getVender = async (params) => {
  return await request.get({ url: `/scm/vender/detail`, params })
}

//审核模块的获取详情
export const getAuditVender = async (params) => {
  return await request.get({ url: '/scm/vender/audit-detail', params })
}

// 新增供应商信息
export const createVender = async (data: VenderVO) => {
  return await request.post({ url: `/scm/vender/create`, data })
}

// 修改供应商信息
export const updateVender = async (data: VenderVO) => {
  return await request.put({ url: `/scm/vender/update`, data })
}

// 删除供应商信息
export const deleteVender = async (id: number) => {
  return await request.delete({ url: `/scm/vender/delete?id=` + id })
}

export const manDeleteVender = async (id: number) => {
  return await request.delete({ url: `/scm/vender/manager-delete?id=` + id })
}

// 导出供应商信息 Excel
export const exportVender = async (params) => {
  return await request.download({ url: `/scm/vender/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//应付供应商
export const getPayableVender = async (params?) => {
  return await request.get({ url: `/scm/vender/get-payable-list`, params })
}

//供应商提交
export const submitVender = async (id) => {
  return await request.put({ url: `/scm/vender/submit?venderId=${id}` })
}
//启用
export const enableVender = async (id) => {
  return await request.put({ url: `/scm/vender/enable?venderId=${id}` })
}
//禁用
export const disEnableVender = async (id) => {
  return await request.put({ url: `/scm/vender/disable?venderId=${id}` })
}
export const toFormalVender = async (data) => {
  return await request.put({ url: `/scm/vender/convert`, data })
}

//根据采购员查询供应商精简列表
export const getVenderSimpleList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list-buyer`, params })
}
//获取付款申请列表
export const getPaymentApplyPage = async (params) => {
  return await request.get({ url: `/scm/payment-apply/page`, params })
}
//删除付款申请
export const deletePaymentApply = async (id: number) => {
  return await request.delete({ url: `/scm/payment-apply/delete?id=` + id })
}
//导出付款申请
export const exportPaymentApply = async (params) => {
  return await request.download({ url: `/scm/payment-apply/export-excel`, params })
}
//供应商变更
export const changeVender = async (data) => {
  return await request.put({ url: `/scm/vender/change`, data })
}

//变更供应商列表
export const changeVenderPage = async (params) => {
  return await request.get({ url: `/scm/vender/change-page`, params })
}

// 变更供应商详情

export const changeVenderDetail = async (params) => {
  return await request.get({ url: `/scm/vender/change-detail`, params })
}

export const auditchangeVenderDetail = async (params) => {
  return await request.get({ url: `/scm/vender/audit-change-detail`, params })
}

// 提交变更任务
export const changeVenderSubmit = async (id) => {
  return await request.put({ url: `/scm/vender/change-submit?venderId=${id}` })
}

// 通过变更任务
export const changeVenderApprove = async (data) => {
  return await request.put({ url: `/scm/vender/change-approve`, data })
}

// 删除变更供应商
export const deleteChangeVender = async (id: number) => {
  return await request.delete({ url: `/scm/vender/change-delete?id=` + id })
}

// 更新变更任务
export const changeVenderUpdate = async (data) => {
  return await request.put({ url: `/scm/vender/change-update`, data })
}

// 获得变更影响列表
export const createVenderEffect = async (data) => {
  return await request.post({ url: `/scm/vender/change-effect`, data })
}
//供应商名称查重
export const getSearchName = async (params) => {
  return await request.get({ url: `/scm/vender/check-name`, params })
}
//反审核
export const revertAudit = async (id) => {
  return await request.put({ url: `/scm/vender/anti-audit?venderId=${id}` })
}

// 付款申请-反提交
export const reverseSubmit = async (id) => {
  return await request.delete({ url: `/scm/payment-apply/cancel?processInstanceId=${id}` })
}

// 付款申请-反审核
export const reversePaymentAudit = async (id) => {
  return await request.put({ url: `/scm/payment-apply/anti-audit?id=${id}` })
}
//作废
export const closePayment = async (data) => {
  return await request.put({ url: `/scm/payment-apply/close`, data })
}
