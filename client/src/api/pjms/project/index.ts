import request from '@/config/axios'

// 查询分页
export const getProjectPage = async (params) => {
  return await request.get({ url: `/pjms/project/page`, params })
}
//新增
export const createProject = async (data) => {
  return await request.post({ url: `/pjms/project/create`, data })
}

export const getProjectInfo = async (params) => {
  return await request.get({ url: `/pjms/project/detail`, params })
}

export const getProjectAuditInfo = async (params) => {
  return await request.get({ url: `/pjms/project/audit-detail`, params })
}
//提交
export const submitProject = async (id) => {
  return await request.put({ url: `/pjms/project/submit?id=${id}` })
}

// 修改
export const updateProject = async (data) => {
  return await request.put({ url: `/pjms/project/update`, data })
}

// 删除
export const deleteProject = async (id: number) => {
  return await request.delete({ url: `/pjms/project/delete?id=` + id })
}

// 导出Excel
export const exportProject = async (params) => {
  return await request.download({ url: `/pjms/project/export-excel`, params })
}
//通过
export const approveProject = async (data) => {
  return await request.put({ url: `/pjms/project/approve`, data })
}
//拒绝
export const rejectProject = async (data) => {
  return await request.put({ url: `/pjms/project/reject`, data })
}
export const doneProject = async (data) => {
  return await request.post({ url: `/pjms/project/done`, data })
}
export const finishProject = async (params) => {
  return await request.post({ url: `/pjms/project/finish`, params })
}
export const rollbackFinishProject = async (params) => {
  return await request.post({ url: `/pjms/project/rollback-finish`, params })
}
// done
