import request from '@/config/axios'

export interface FeesVO {
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

// 查询财务付款信息分页
export const getFeesPage = async (params) => {
  return await request.get({ url: `/fms/payment/page`, params })
}

// 查询财务付款信息详情
export const getFees = async (params) => {
  return await request.get({ url: `/fms/payment/detail`, params })
}

//审核模块的获取详情
export const getAuditFees = async (params) => {
  return await request.get({ url: '/fms/payment/audit-detail', params })
}

// 新增财务付款信息
export const createFees = async (data: FeesVO) => {
  return await request.post({ url: `/fms/payment/create`, data })
}

// 修改财务付款信息
export const updateFees = async (data: FeesVO) => {
  return await request.put({ url: `/fms/payment/update`, data })
}

// 删除财务付款信息
export const deleteFees = async (id: number) => {
  return await request.delete({ url: `/fms/payment/delete?id=` + id })
}

// 导出财务付款信息 Excel
export const exportFees = async (params) => {
  return await request.download({ url: `/fms/payment/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//财务付款提交
export const submitFees = async (id) => {
  return await request.put({ url: `/fms/payment/submit?paymentId=${id}` })
}

//支付确认
export const confirmFees = async (data) => {
  return await request.put({ url: `/fms/payment/confirm`, data })
}

//计划付款提交
export const submitPlanFees = async (data) => {
  return await request.put({ url: `/fms/payment/plan-payment`, data })
}
//批量付款
export const batchPayment = async (data) => {
  return await request.put({ url: `/fms/payment/batch-confirm`, data })
}

export const closeApi = async (id) => {
  return await request.put({ url: `/fms/payment/close?id=${id}` })
}
export const cancelPlanPaymentApi = async (id) => {
  return await request.put({ url: `/fms/payment/plan-payment-cancel?id=${id}` })
}

export const batchDirectPayment = async (data) => {
  return await request.put({ url: `/fms/payment/batch-payment`, data })
}

export const batchPlanPayment = async (data) => {
  return await request.put({ url: `/fms/payment/batch-plan-payment`, data })
}

export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}
