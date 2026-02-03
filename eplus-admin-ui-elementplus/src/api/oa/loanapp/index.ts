import { EplusAuditable } from './../../../types/eplus.d'
import request from '@/config/axios'

export interface LoanAppVO extends EplusAuditable {
  id: number
  purpose: string
  loanDeptId: number
  loanDeptName: string
  applyerId: number
  applyerName: string
  repayStatus: string
  repayDate: Date
  amount: {
    amount: string | number
    currency: string
  }
  currency: string
  transferStatus: string
  loanDate: Date
  transferAmount: number
  repayAmount: number
  bank: string
  bankaccount: string
  accountname: string
  auditStatus: number
  submitFlag?: number
  exchangeRate?: number // 汇率
  userInfo: {
    nickname: string
  }
}

// 查询借款申请单分页
export const getLoanAppPage = async (params) => {
  return await request.get({ url: `/oa/loan-app/page`, params })
}

// 查询借款申请单详情
export const getLoanApp = async (params) => {
  return await request.get({ url: `/oa/loan-app/detail`, params })
}

// 审核模块的获取详情
export const getAuditLoanApp = async (params) => {
  return await request.get({ url: `/oa/loan-app/audit-detail`, params })
}

// 新增借款申请单
export const createLoanApp = async (data: LoanAppVO) => {
  return await request.post({ url: `/oa/loan-app/create`, data })
}

// 修改借款申请单
export const updateLoanApp = async (data: LoanAppVO) => {
  return await request.put({ url: `/oa/loan-app/update`, data })
}

// 删除借款申请单
export const deleteLoanApp = async (id: number) => {
  return await request.delete({ url: `/oa/loan-app/delete?id=` + id })
}

// 导出借款申请单 Excel
export const exportLoanApp = async (params) => {
  return await request.download({ url: `/oa/loan-app/export-excel`, params })
}

//提交审核借款单
export const submitLoanApp = async (id) => {
  return await request.put({ url: `/oa/loan-app/submit?loanAppId=${id}` })
}
// 查询用户银行信息
export const bankaccountInfo = async (id) => {
  return await request.get({ url: `/system/user/get-bankaccount?id=${id}` })
}

//获取公司主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}

//获取不公司主体
export const getUnProcessCompanyList = async () => {
  return await request.get({ url: `/system/company/get-unprocess-list` })
}

//打印
export const print = async (params) => {
  return await request.get({ url: `/oa/loan-app/print`, params })
}
