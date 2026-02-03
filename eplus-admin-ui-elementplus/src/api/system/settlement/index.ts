import request from '@/config/axios'

export interface SettlementVO {
  id: number
  name: string
  nameEng: string
}

// 查询收款方式分页
export const getSettlementPage = async (params) => {
  return await request.get({ url: `/infra/settlement/page`, params })
}

// 查询收款方式详情
export const getSettlement = async (id: number) => {
  return await request.get({ url: `/infra/settlement/get?id=` + id })
}

// 新增收款方式
export const createSettlement = async (data: SettlementVO) => {
  return await request.post({ url: `/infra/settlement/create`, data })
}

// 修改收款方式
export const updateSettlement = async (data: SettlementVO) => {
  return await request.put({ url: `/infra/settlement/update`, data })
}

// 删除收款方式
export const deleteSettlement = async (id: number) => {
  return await request.delete({ url: `/infra/settlement/delete?id=` + id })
}

// 导出收款方式 Excel
export const exportSettlement = async (params) => {
  return await request.download({ url: `/infra/settlement/export-excel`, params })
}
