import request from '@/config/axios'

// 查询分页
export const getFeeSharePage = async (params) => {
  return await request.get({ url: `/oa/fee-share/page`, params })
}
//更新
export const updateFeeShare = async (data) => {
  return await request.put({ url: `/oa/fee-share/batch-update`, data })
}

// 查询费用共享详情
export const getFeeShareDetail = async (params) => {
  return await request.get({ url: `/oa/fee-share/detail`, params })
}
