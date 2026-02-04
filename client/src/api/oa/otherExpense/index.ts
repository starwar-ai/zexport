import request from '@/config/axios'

// 查询一般费用报销分页
export const getOtherReimbPage = async (params) => {
  return await request.get({ url: `/oa/reimb/other/page`, params })
}

// 查询一般费用申请详情
export const getOtherReimb = async (id) => {
  return await request.get({ url: `/oa/reimb/other/detail?id=${id}` })
}
export const getOtherReimbProcess = async (params) => {
  return await request.get({ url: `/oa/reimb/other/process-detail`, params })
}

//新增
export const createOtherReimb = async (data) => {
  return await request.post({ url: `/oa/reimb/other/create`, data })
}

// 修改
export const updateOtherReimb = async (data) => {
  return await request.put({ url: `/oa/reimb/other/update`, data })
}

// 删除
export const deleteOtherReimb = async (id: number) => {
  return await request.delete({ url: `/oa/reimb/other/delete?id=` + id })
}

// 导出Excel
export const exportOtherReimb = async (params) => {
  return await request.download({ url: `/oa/reimb/other/export-excel`, params })
}

export const submitOtherReimb = async (id) => {
  return await request.put({ url: `/oa/reimb/other/submit?OtherReimbId=${id}` })
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
// export const getOtherAppSimpleList = async (params) => {
//   return await request.get({ url: `/oa/Other-app/get-simple-list`, params })
// }

//获取住宿标准
export const getLodgingSubsidy = async () => {
  return await request.get({ url: `/oa/reimb/other/get-lodging-subsidy` })
}

//获取一般费用报销
export const getOtherStandard = async () => {
  return await request.get({ url: `/oa/reimb/other/get-Other-standard` })
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
  return await request.get({ url: `/oa/reimb/other/print`, params })
}
//作废
export const closeOther = async (data) => {
  return await request.put({ url: `/oa/reimb/other/close`, data })
}
