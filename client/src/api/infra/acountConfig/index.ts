import request from '@/config/axios'

export interface AcountConfigVO {
  id: number
}

// 查询财务付款信息分页
export const getAcountConfigPage = async (params) => {
  return await request.get({ url: `/system/company/page`, params })
}

// 查询财务付款信息详情
export const getAcountConfig = async (params) => {
  return await request.get({ url: `/system/company/get`, params })
}

// 新增财务付款信息
export const createAcountConfig = async (data: AcountConfigVO) => {
  return await request.post({ url: `/system/company/create`, data })
}

// 修改财务付款信息
export const updateAcountConfig = async (data: AcountConfigVO) => {
  return await request.put({ url: `/system/company/update`, data })
}

// 删除财务付款信息
export const deleteAcountConfig = async (id: number) => {
  return await request.delete({ url: `/system/company/delete?id=` + id })
}

// 导出财务付款信息 Excel
export const exportAcountConfig = async (params) => {
  return await request.download({ url: `/system/company/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//财务付款提交
export const submitAcountConfig = async (id) => {
  return await request.put({ url: `/system/company/submit?receiptId=${id}` })
}

//支付确认
export const getBankAcountConfig = async (params?) => {
  return await request.get({ url: `/system/company/get-simple-msg`, params })
}
