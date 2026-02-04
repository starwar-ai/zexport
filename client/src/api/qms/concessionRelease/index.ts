import request from '@/config/axios'

// 查询分页
export const getConcessionReleasePage = async (params) => {
  return await request.get({ url: `/scm/concession-release/page`, params })
}

// 查询详情
export const getConcessionRelease = async (params) => {
  return await request.get({ url: `/scm/concession-release/detail`, params })
}

//审核模块的获取详情
export const getAuditConcessionRelease = async (params) => {
  return await request.get({ url: '/scm/concession-release/audit-detail', params })
}

// 新增
export const createConcessionRelease = async (data) => {
  return await request.post({ url: `/scm/concession-release/create`, data })
}

// 修改
export const updateConcessionRelease = async (data) => {
  return await request.put({ url: `/scm/concession-release/update`, data })
}

// 删除
export const deleteConcessionRelease = async (id: number) => {
  return await request.delete({ url: `/scm/concession-release/delete?id=` + id })
}

//审核通过
export const approveConcessionRelease = async (data) => {
  return await request.put({ url: `/scm/concession-release/approve`, data })
}
//审核拒绝
export const rejectConcessionRelease = async (data) => {
  return await request.put({ url: `/scm/concession-release/reject`, data })
}
