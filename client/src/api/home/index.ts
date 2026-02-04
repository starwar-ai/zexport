import request from '@/config/axios'

// 获取首页页签
export const getHomeTabList = async (params) => {
  return await request.get({ url: '/system/home-tab/list', params })
}
// 新增首页页签
export const createHomeTabs = async (data) => {
  return await request.post({ url: '/system/home-tab/create', data })
}
// 页签排序
export const homeTabsSort = async (data) => {
  return await request.put({ url: '/system/home-tab/sort', data })
}

// 更新首页页签
export const homeTabUpdate = async (data) => {
  return await request.put({ url: '/system/home-tab/update', data })
}

// 删除首页页签
export const deleteHemoTab = async (id: number | string) => {
  return await request.delete({ url: '/system/home-tab/delete?id=' + id })
}

// 获取首页卡片列表
export const getHomeCardList = async (params) => {
  return await request.get({ url: '/home/card/list', params })
}

// 首页创建卡片
export const createCard = async (data) => {
  return await request.post({ url: '/home/card/create', data })
}

// 删除卡片
export const deleteHemoCard = async (id: number | string) => {
  return await request.delete({ url: '/home/card/delete?id=' + id })
}

// 更新首页卡片
export const cardUpdate = async (data) => {
  return await request.put({ url: '/home/card/update', data })
}

// 卡片位置变更
export const cardPosition = async (data) => {
  return await request.put({ url: '/home/card/update/position', data })
}

// 票夹子列表
export const invoiceHolderPage = async (params) => {
  return await request.get({ url: '/home/invoice-holder/page', params })
}
// 新增票夹子
export const createInvoiceHolder = async (data) => {
  return await request.post({ url: '/home/invoice-holder/create', data })
}

// 删除发票夹
export const deleteHemoInvoice = async (id: number | string) => {
  return await request.delete({ url: '/home/invoice-holder/delete?id=' + id })
}

// 更新发票夹
export const updateInvoice = async (data) => {
  return await request.put({ url: '/home/invoice-holder/update', data })
}

// 上传快递单号
export const uploadNumber = async (params) => {
  return await request.post({ url: '/ems/send/upload-number', params })
}

// // 导入execl
export const sendBillImport = async (data) => {
  return await request.post({ url: '/ems/send-bill/import', data })
}

// 回填费用-批量
export const backfillImport = async (data) => {
  return await request.post({ url: '/ems/send/import', data })
}

// 回填费用-单个
export const updateCost = async (data) => {
  return await request.post({ url: '/ems/send/update-cost', data })
}

// 获得快递公司精简列表
export const getSimpleList = async () => {
  return await request.get({
    url: '/scm/vender/get-simple-list',
    params: { pageSize: 100, pageNo: 1, administrationVenderType: 1 }
  })
}

// 更新费用归集
export const updateFeeShare = async (data) => {
  return await request.put({ url: '/ems/send/fee-share-update', data })
}

export const purchaseList = async (params) => {
  return await request.get({ url: '/scm/purchase-contract/get-simple-list', params })
}

export const contractList = async (params) => {
  return await request.get({ url: '/sms/export/sale-contract/get-simple-list', params })
}

//

// 修改用户当前时间
export const updateInternationalTime = async (data) => {
  return await request.put({ url: '/system/user/update-time-preferences', data })
}

// 获取用户当前时间
export const getInternationalTime = async (params) => {
  return await request.get({ url: '/system/user/get-time-preferences', params })
}

// 获取所有卡片
export const getAllCard = async (params) => {
  return await request.get({ url: '/home/card/page', params })
}

// 赋予角色卡片
export const submitRoleCard = async (data) => {
  return await request.post({ url: '/system/permission/assign-role-card', data })
}

// 获取首页右侧弹框卡片列表
export const getHomeBaseList = async (params) => {
  return await request.get({ url: '/home/card/base-list', params })
}
