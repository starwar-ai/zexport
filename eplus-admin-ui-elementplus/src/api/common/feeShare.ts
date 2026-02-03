import request from '@/config/axios'

export const updateFeeShare = async (data) => {
  return await request.put({ url: '/ems/send/fee-share-update', data })
}

export const purchaseList = async (params) => {
  return await request.get({ url: '/scm/purchase-contract/get-simple-list', params })
}

export const contractList = async (params) => {
  return await request.get({ url: '/sms/export/sale-contract/get-simple-list', params })
}

//展会列表
export const exhibitionList = async (params) => {
  return await request.get({
    url: '/exms/exhibition/get-simple-list',
    params: { ...params, auditStatus: 2 }
  })
}

//项目列表
export const projectList = async (params) => {
  return await request.get({ url: '/pjms/project/get-simple-list', params })
}

// 具体名称精简列表

export const descCreate = async (data) => {
  return await request.post({ url: '/oa/fee-share-desc/update-all', data })
}
export const descList = async (params) => {
  return await request.get({ url: '/oa/fee-share-desc/get-simple-list', params })
}

export const descDel = async (id) => {
  return await request.delete({ url: `/oa/fee-share-desc/delete?id=${id}` })
}
