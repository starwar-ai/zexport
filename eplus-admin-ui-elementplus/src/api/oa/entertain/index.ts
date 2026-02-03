import request from '@/config/axios'

// 查询分页
export const getEntertainPage = async (params) => {
  return await request.get({ url: `/oa/reimb/entertain/page`, params })
}
//新增
export const createEntertain = async (data) => {
  return await request.post({ url: `/oa/reimb/entertain/create`, data })
}

export const getEntertainInfo = async (params) => {
  return await request.get({ url: `/oa/reimb/entertain/detail`, params })
}
export const getAuditEntertainInfo = async (params) => {
  return await request.get({ url: `/oa/reimb/entertain/process-detail`, params })
}

// 修改
export const updateEntertain = async (data) => {
  return await request.put({ url: `oa/reimb/entertain/update`, data })
}

// 删除
export const deleteEntertain = async (id: number) => {
  return await request.delete({ url: `oa/reimb/entertain/delete?id=` + id })
}

// 导出Excel
export const exportEntertain = async (params) => {
  return await request.download({ url: `oa/reimb/entertain/export-excel`, params })
}

//提交
export const submitEntertain = async (id) => {
  return await request.put({ url: `oa/reimb/entertain/submit?entertainReimbId=${id}` })
}

//通过
export const approveEntertain = async (data) => {
  return await request.put({ url: `oa/reimb/entertain/approve`, data })
}
//不通过
export const rejectEntertain = async (data) => {
  return await request.put({ url: `oa/reimb/entertain/reject`, data })
}

//查询供应商列表
export const getVenderList = async (name) => {
  return await request.get({ url: `/scm/vender/get-payable-list?venderName=${name}` })
}
//查询客户列表
export const getCustList = async (name) => {
  return await request.get({ url: `/crm/cust/get-cust-bank-list?custName=${name}` })
}

//打印
export const print = async (params) => {
  return await request.get({ url: `/oa/reimb/entertain/print`, params })
}

//申请单查询
export const reimbList = async (params) => {
  return await request.get({ url: `/oa/expense/page`, params })
}

// 根据申请单code 查询费用归集
export const batchGetFeeShare = async (params) => {
  return await request.get({ url: `/oa/fee-share/batch-get`, params })
}

//作废
export const closeEntertain = async (data) => {
  return await request.put({ url: `oa/reimb/entertain/close`, data })
}
