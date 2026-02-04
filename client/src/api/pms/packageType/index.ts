import request from '@/config/axios'

// 查询分页
export const getPackageTypePage = async (params) => {
  return await request.get({ url: `/pms/package-type/page`, params })
}

export const getPackageTypeInfo = async (id) => {
  return await request.get({ url: `/pms/package-type/detail?id=` + id })
}

//新增
export const createPackageType = async (data) => {
  return await request.post({ url: `/pms/package-type/create`, data })
}

// 修改
export const updatePackageType = async (data) => {
  return await request.put({ url: `/pms/package-type/update`, data })
}

// 删除
export const deletePackageType = async (id: number) => {
  return await request.delete({ url: `/pms/package-type/delete?id=` + id })
}
