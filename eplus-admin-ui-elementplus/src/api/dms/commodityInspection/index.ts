import request from '@/config/axios'
// 查询分页
export const getCommodityInspectionPage = async (params) => {
  return await request.get({ url: `/dms/commodity-inspection/page`, params })
}
//新增
export const createCommodityInspection = async (data) => {
  return await request.post({ url: `/dms/commodity-inspection/create`, data })
}
export const getCommodityInspectionInfo = async (params) => {
  return await request.get({ url: `/dms/commodity-inspection/detail`, params })
}
// 修改
export const updateCommodityInspection = async (data) => {
  return await request.put({ url: `/dms/commodity-inspection/update`, data })
}
// 删除
export const deleteCommodityInspection = async (id: number) => {
  return await request.delete({ url: `/dms/commodity-inspection/delete?id=${id}` })
}
// 导出Excel

export const exportCommodityInspection = async (params) => {
  return await request.download({
    url: `/dms/commodity-inspection/export-excel`,
    params,
    neverLoading: true
  })
}
