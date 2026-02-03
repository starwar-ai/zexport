import request from '@/config/axios'

// export interface TravelAppVO {
//   id: number
//   purpose: string
//   dest: string
//   result: number
//   startTime: Date
//   endTime: Date
//   duration: number
//   transportationType: string
//   companions: string
//   createTime: Date
// }

// 查询一般费用报销分页
export const getGeneralReimbPage = async (params) => {
  return await request.get({ url: `/oa/reimb/general/page`, params })
}

// 查询一般费用申请详情
export const getGeneralReimb = async (id) => {
  return await request.get({ url: `/oa/reimb/general/detail?id=${id}` })
}
export const getGeneralReimbProcess = async (params) => {
  return await request.get({ url: `/oa/reimb/general/process-detail`, params })
}

//新增
export const createGeneralReimb = async (data) => {
  return await request.post({ url: `/oa/reimb/general/create`, data })
}

// 修改
export const updateGeneralReimb = async (data) => {
  return await request.put({ url: `oa/reimb/general/update`, data })
}

// 删除
export const deleteGeneralReimb = async (id: number) => {
  return await request.delete({ url: `oa/reimb/general/delete?id=` + id })
}

// 导出Excel
export const exportGeneralReimb = async (params) => {
  return await request.download({ url: `oa/reimb/general/export-excel`, params })
}

export const submitGeneralReimb = async (id) => {
  return await request.put({ url: `oa/reimb/general/submit?generalReimbId=${id}` })
}

//获取客户列表
export const getCustSimpleList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}

//获取供应商列表
export const getVenderSimpleList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list`, params })
}

//获取一般费用单列表
export const getGeneralAppSimpleList = async (params) => {
  return await request.get({ url: `/oa/general-app/get-simple-list`, params })
}

//获取住宿标准
export const getLodgingSubsidy = async () => {
  return await request.get({ url: `/oa/reimb/general/get-lodging-subsidy` })
}

//获取一般费用报销
export const getGeneralStandard = async () => {
  return await request.get({ url: `/oa/reimb/general/get-general-standard` })
}

//获取借款单
export const getLoanSimpleList = async (params) => {
  return await request.get({ url: `/oa/loan-app/get-simple-list`, params })
}

//获取公司主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}

// 查询用户银行信息
export const bankaccountInfo = async (id) => {
  return await request.get({ url: `/system/user/get-bankaccount?id=${id}` })
}

//打印
export const print = async (params) => {
  return await request.get({ url: `/oa/reimb/general/print`, params })
}
//作废
export const closeGeneral = async (data) => {
  return await request.put({ url: `oa/reimb/general/close`, data })
}
