import request from '@/config/axios'

// 查询分页
export const getExhibitionPage = async (params) => {
  return await request.get({ url: `/exms/exhibition/page`, params })
}
//新增
export const createExhibition = async (data) => {
  return await request.post({ url: `/exms/exhibition/create`, data })
}

export const getExhibitionInfo = async (params) => {
  return await request.get({ url: `/exms/exhibition/detail`, params })
}

export const getExhibitionAuditInfo = async (params) => {
  return await request.get({ url: `/exms/exhibition/audit-detail`, params })
}
//提交
export const submitExhibition = async (id) => {
  return await request.put({ url: `/exms/exhibition/submit?id=${id}` })
}

// 修改
export const updateExhibition = async (data) => {
  return await request.put({ url: `/exms/exhibition/update`, data })
}

// 删除
export const deleteExhibition = async (id: number) => {
  return await request.delete({ url: `/exms/exhibition/delete?id=` + id })
}

// 导出Excel
export const exportExhibition = async (params) => {
  return await request.download({ url: `/exms/exhibition/export-excel`, params })
}
//通过
export const approveExhibition = async (data) => {
  return await request.put({ url: `/exms/exhibition/approve`, data })
}
//拒绝
export const rejectExhibition = async (data) => {
  return await request.put({ url: `/exms/exhibition/reject`, data })
}
export const doneExhibition = async (data) => {
  return await request.post({ url: `/exms/exhibition/done`, data })
}
export const finishExhibition = async (params) => {
  return await request.post({ url: `/exms/exhibition/finish`, params })
}
export const rollbackFinishExhibition = async (params) => {
  return await request.post({ url: `/exms/exhibition/rollback-finish`, params })
}

//获得展会精简类别
export const getSimpleExhibitionPage = async (params) => {
  return await request.get({
    url: `/exms/exhibition/get-simple-list`,
    params: { ...params, auditStatus: 2 }
  })
}
// done
