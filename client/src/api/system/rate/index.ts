import request from '@/config/axios'

export interface CurrencysRateVO {
  id: number
  dailyCurrDate: string
  dailyCurrName: string
  dailyCurrRate: number
  dailyCurrSource: number
  dailyCurrMidRate: number
}

// 查询动态汇率分页
export const getCurrencysRatePage = async () => {
  return await request.get({ url: `/infra/rate/get-simple-map` })
}

// 查询动态汇率详情
export const getCurrencysRate = async (id: number) => {
  return await request.get({ url: `/infra/rate/get?id=` + id })
}
// 导出动态汇率 Excel
export const exportCurrencysRate = async (params) => {
  return await request.download({ url: `/infra/rate/export-excel`, params })
}
