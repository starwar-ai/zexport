import request from '@/config/axios'
// 查询分页
export const getForwarderCompanyPage = async (params) => {
  return await request.get({ url: `/dms/forwarder-company-info/page`, params })
}
//新增
export const createForwarderCompany = async (data) => {
  return await request.post({ url: `/dms/forwarder-company-info/create`, data })
}

export const getForwarderCompanyInfo = async (params) => {
  return await request.get({ url: `/dms/forwarder-company-info/detail`, params })
}
// 修改
export const updateForwarderCompany = async (data) => {
  return await request.put({ url: `/dms/forwarder-company-info/update`, data })
}
// enable  启用  disable  停用
export const updateForwarderCompanyStatus = async (type, id) => {
  return await request.put({ url: `/dms/forwarder-company-info/${type}/${id}` })
}

// 删除
export const deleteForwarderCompany = async (id: number) => {
  return await request.delete({ url: `/dms/forwarder-company-info/delete?id=${id}` })
}

// 导出Excel
export const exportForwarderCompany = async (params) => {
  return await request.download({ url: `/dms/forwarder-company-info/export-excel`, params })
}
//费用归集查询详情
export const forwarderFeeDetail = async (params) => {
  return await request.get({ url: `/dms/forwarder-fee/detail`, params })
}
