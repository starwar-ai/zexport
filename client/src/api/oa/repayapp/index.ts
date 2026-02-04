import request from '@/config/axios'

export interface RepayAppVO {
  id: number
  loanAppId: number
  code: string
  printFlag: number
  printTimes: number
  auditStatus: number
  amount: number
  remark: string
  repayTime: Date
  repayerId: number
}

// 查询还款单分页
export const getRepayAppPage = async (params) => {
  return await request.get({ url: `/oa/repay-app/page`, params })
}

// 查询还款单详情
export const getRepayApp = async (params) => {
  return await request.get({ url: `/oa/repay-app/detail`, params })
}

//审核模块的获取详情
export const getAuditRepayApp = async (params) => {
  return await request.get({ url: '/oa/repay-app/audit-detail', params })
}

// 新增还款单
export const createRepayApp = async (data) => {
  return await request.post({ url: `/oa/repay-app/create`, data })
}

// 修改还款单
export const updateRepayApp = async (data) => {
  return await request.put({ url: `/oa/repay-app/update`, data })
}

// 删除还款单
export const deleteRepayApp = async (id: number) => {
  return await request.delete({ url: `/oa/repay-app/delete?id=` + id })
}

// 导出还款单 Excel
export const exportRepayApp = async (params) => {
  return await request.download({ url: `/oa/repay-app/export-excel`, params })
}

//获取借款单
export const getLoanSimpleList = async (params) => {
  return await request.get({ url: `/oa/loan-app/get-simple-list`, params })
}
// 查询用户银行信息
export const bankaccountInfo = async (id) => {
  return await request.get({ url: `/system/user/get-bankaccount?id=${id}` })
}
//获取还款主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}
//提交
export const submitRepayApp = async (params) => {
  return await request.put({ url: `/oa/repay-app/submit`, params })
}
//审核通过
export const approveRepayApp = async (data) => {
  return await request.put({ url: `/oa/repay-app/approve`, data })
}
//审核拒绝
export const rejectRepayApp = async (data) => {
  return await request.put({ url: `/oa/repay-app/reject`, data })
}
//通过code获取还款单详情
export const getRepayAppBycode = async (code) => {
  return await request.get({ url: `/oa/repay-app/detail-by-code?code=${code}` })
}
