import request from '@/config/axios'

// 查询借款申请单分页
export const getVenderLoanPage = async (params) => {
  return await request.get({ url: `/scm/vender-loan/page`, params })
}

// 查询借款申请单详情
export const getVenderLoan = async (params) => {
  return await request.get({ url: `/scm/vender-loan/detail`, params })
}

// 审核模块的获取详情
export const getAuditVenderLoan = async (params) => {
  return await request.get({ url: `/scm/vender-loan/audit-detail`, params })
}

// 新增借款申请单
export const createVenderLoan = async (data) => {
  return await request.post({ url: `/scm/vender-loan/create`, data })
}

// 修改借款申请单
export const updateVenderLoan = async (data) => {
  return await request.put({ url: `/scm/vender-loan/update`, data })
}

// 删除借款申请单
export const deleteVenderLoan = async (id: number) => {
  return await request.delete({ url: `/scm/vender-loan/delete?id=` + id })
}

// 导出借款申请单 Excel
export const exportVenderLoan = async (params) => {
  return await request.download({ url: `/scm/vender-loan/export-excel`, params })
}

//提交审核借款单
export const submitVenderLoan = async (id) => {
  return await request.put({ url: `/scm/vender-loan/submit?loanAppId=${id}` })
}
// 查询用户银行信息
export const bankaccountInfo = async (id) => {
  return await request.get({ url: `/system/user/get-bankaccount?id=${id}` })
}

//获取公司主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}
