import request from '@/config/axios'
// 查询分页
export const getDeclarationPage = async (params) => {
  return await request.get({ url: `/dms/declaration/page`, params })
}
//详情
export const getDeclarationInfo = async (params) => {
  return await request.get({ url: `/dms/declaration/detail`, params })
}

// 修改
export const updateDeclaration = async (data) => {
  // return await request.put({ url: `/dms/declaration/update`, data })
  return await request.put({ url: `/dms/declaration/update-group`, data })
}
// 删除
export const deleteDeclaration = async (id: number) => {
  return await request.delete({ url: `/dms/declaration/delete?id=${id}` })
}

// 导出Excel
export const exportDeclaration = async (params) => {
  return await request.download({
    url: `/dms/declaration/export-excel`,
    params,
    neverLoading: true
  })
}
