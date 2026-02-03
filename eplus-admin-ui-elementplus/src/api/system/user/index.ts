import request from '@/config/axios'

export interface UserVO {
  id: number
  code: string
  username: string
  nickname: string
  deptId: number
  deptName: string
  postIds: string[]
  email: string
  mobile: string
  sex: number
  avatar: string
  loginIp: string
  status: number
  remark: string
  loginDate: Date
  createTime: Date
  userId?: number
}

// 查询用户管理列表
export const getUserPage = (params: PageParam) => {
  return request.get({ url: '/system/user/page', params })
}
export const getUserSimplePage = (params: PageParam) => {
  return request.get({ url: '/system/user/simple-page', params })
}

// 查询所有用户列表
export const getAllUser = () => {
  return request.get({ url: '/system/user/all' })
}

// 查询用户详情
export const getUser = (id: number) => {
  return request.get({ url: '/system/user/get?id=' + id })
}

// 新增用户
export const createUser = (data: UserVO) => {
  return request.post({ url: '/system/user/create', data })
}

// 修改用户
export const updateUser = (data: UserVO) => {
  return request.put({ url: '/system/user/update', data })
}

// 删除用户
export const deleteUser = (id: number) => {
  return request.delete({ url: '/system/user/delete?id=' + id })
}

// 导出用户
export const exportUser = (params) => {
  return request.download({ url: '/system/user/export', params })
}
//导出用户银行信息模板
export const exportBankTemplate = () => {
  return request.download({ url: '/system/user/export-bank-template' })
}

// 下载用户导入模板
export const importUserTemplate = () => {
  return request.download({ url: '/system/user/get-bank-import-template' })
}

// 用户密码重置
export const resetUserPwd = (id: number, password: string) => {
  const data = {
    id,
    password
  }
  return request.put({ url: '/system/user/update-password', data: data })
}

// 用户状态修改
export const updateUserStatus = (id: number, status: number) => {
  const data = {
    id,
    status
  }
  return request.put({ url: '/system/user/update-status', data: data })
}

// 获取用户精简信息列表
export const getSimpleUserList = (): Promise<UserVO[]> => {
  return request.get({ url: '/system/user/simple-list?status=1' })
}

// 查询用户精简详情
export const getSimpleUser = (id: number) => {
  return request.get({ url: '/system/user/simple-get?id=' + id })
}

//设置列表数据
export const updateSetPreferences = (data) => {
  return request.put({ url: '/system/user/update-set-preferences', data: data })
}

export const getPreferences = (key) => {
  return request.get({ url: '/system/user/get-set-preferences?pageKey=' + key })
}
