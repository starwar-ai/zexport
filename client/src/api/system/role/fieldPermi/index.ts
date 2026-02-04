import request from '@/config/axios'

export interface RoleVO {
  tableName: string
  tableComment: string
  fieldName: string
  fieldComment: string
  fieldType: number
  dictFlag: number
  dictType: number
  createTime: Date
  fieldPermissionFlag: number | boolean
}

export interface UpdateStatusReqVO {
  id: number
  status: number
}

// 查询系统表列表
export const getSysTableName = async () => {
  return await request.get({ url: '/system/field/table' })
}

// 查询系统字段列表
export const getSysFieldsList = async (params) => {
  return await request.get({ url: '/system/field/get', params })
}

// 更新字段角色关联
export const setFields2Role = async (data) => {
  return await request.put({ url: '/system/role-field/update', data: data })
}
