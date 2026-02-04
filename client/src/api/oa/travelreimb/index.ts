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

// 查询出差报销分页
export const getTravelReimbPage = async (params) => {
  return await request.get({ url: `/oa/reimb/travel/page`, params })
}

// 查询出差申请详情
export const getTravelReimb = async (id) => {
  return await request.get({ url: `/oa/reimb/travel/detail?id=${id}` })
}
export const getTravelReimbProcess = async (params) => {
  return await request.get({ url: `/oa/reimb/travel/process-detail`, params })
}

//新增
export const createTravelReimb = async (data) => {
  return await request.post({ url: `/oa/reimb/travel/create`, data })
}

// 修改
export const updateTravelReimb = async (data) => {
  return await request.put({ url: `oa/reimb/travel/update`, data })
}

// 删除
export const deleteTravelReimb = async (id: number) => {
  return await request.delete({ url: `oa/reimb/travel/delete?id=` + id })
}

// 导出Excel
export const exportTravelReimb = async (params) => {
  return await request.download({ url: `oa/reimb/travel/export-excel`, params })
}

export const submitTravelReimb = async (id) => {
  return await request.put({ url: `oa/reimb/travel/submit?travelReimbId=${id}` })
}

//获取客户列表
export const getCustSimpleList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}

//获取供应商列表
export const getVenderSimpleList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list`, params })
}

//获取出差单列表
export const getTravelAppSimpleList = async (params) => {
  return await request.get({ url: `/oa/travel-app/get-simple-list`, params })
}

//获取住宿标准
export const getLodgingSubsidy = async () => {
  return await request.get({ url: `/oa/reimb/travel/get-lodging-subsidy` })
}
//查询每公里对应金额
export const getMileageSubsidy = async () => {
  return await request.get({ url: `/oa/reimb/travel/get-mileage-standard` })
}

//获取出差报销
export const getTravelStandard = async () => {
  return await request.get({ url: `/oa/reimb/travel/get-travel-standard` })
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
  return await request.get({ url: `/oa/reimb/travel/print`, params })
}

//作废
export const closeTravel = async (data) => {
  return await request.put({ url: `oa/reimb/travel/close`, data })
}
