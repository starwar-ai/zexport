import request from '@/config/axios'
import { useUserStore } from '@/store/modules/user'

const userInfo = useUserStore()

// 查询分页
export const getEntertainApplyPage = async (params) => {
  return await request.get({ url: `/oa/entertain-apply/page`, params })
}

export const getApplyList = async (params) => {
  return await request.get({
    url: `/oa/apply/get-simple-list`,
    params: { ...params, isApplyExpense: 0, auditStatus: 2, applierId: userInfo.user?.id }
  })
}
//新增
export const createEntertainApply = async (data) => {
  return await request.post({ url: `/oa/entertain-apply/create`, data })
}

export const getEntertainApplyAuditInfo = async (params) => {
  return await request.get({ url: `/oa/entertain-apply/audit-detail`, params })
}

export const getEntertainApplyInfo = async (params) => {
  return await request.get({ url: `/oa/entertain-apply/detail`, params })
}

// 修改
export const updateEntertainApply = async (data) => {
  return await request.put({ url: `oa/entertain-apply/update`, data })
}

// 删除
export const deleteEntertainApply = async (id: number) => {
  return await request.delete({ url: `oa/entertain-apply/delete?id=` + id })
}

// 导出Excel
export const exportEntertainApply = async (params) => {
  return await request.download({ url: `oa/entertain-apply/export-excel`, params })
}

//提交
export const submitEntertainApply = async (id) => {
  return await request.put({ url: `oa/entertain-apply/submit?id=${id}` })
}

//通过
export const approveEntertainApply = async (data) => {
  return await request.put({ url: `oa/entertain-apply/approve`, data })
}
//不通过
export const rejectEntertainApply = async (data) => {
  return await request.put({ url: `oa/entertain-apply/reject`, data })
}
//作废
export const closeEntertainApply = async (id) => {
  return await request.put({ url: `oa/entertain-apply/close?id=${id}` })
}
