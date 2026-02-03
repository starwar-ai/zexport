import request from '@/config/axios'

//获取客户列表
export const getCustSimpleList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}

//获取供应商列表
export const getVenderSimpleList = async (params) => {
  return await request.get({ url: `/scm/vender/get-simple-list`, params })
}

//获取公司主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}

export const getNotInnerCompanySimpleList = async () => {
  return await request.get({ url: `system/company/get-simple-list-not-inner` })
}

// 仓库
export const getStockList = async () => {
  return await request.get({ url: `/wms/warehouse/get-simple-list` })
}

// 查询财务科目
export const getSubject = async (params) => {
  return await request.get({ url: `/oa/dict-subject/getSubject`, params })
}

// 查询资质信息
export const getQualificatioPage = async (params) => {
  return await request.get({ url: `/scm/qualification/page`, params })
}

export const regionList = async (params) => {
  return await request.get({ url: `/infra/country-info/get-region`, params })
}

export const getInvoiceRate = async (params) => {
  return await request.get({ url: `/infra/config/get-invoice-rate`, params })
}

export const getConfigJson = async (params) => {
  return await request.get({ url: `/infra/config/get-json`, params })
}

export const getConfigKey = (configKey: string) => {
  return request.get({ url: '/infra/config/get-value-by-key?key=' + configKey })
}

export const uploadApi = (data) => {
  return request.upload({ url: '/infra/file/upload', data: data })
}
// //关联单据接口
// export const getRelatedOrder = async (params) => {
//   return await request.get({ url: `/system/orderlink/get-tree-by-link-code`, params })
// }

// 包装方式列表
export const packageTypeList = async (params) => {
  return await request.get({ url: `/pms/package-type/get-simple-list`, params })
}

//关联单据列表
export const orderlinkList = async (params) => {
  return await request.get({ url: `/system/orderlink/get-order-link-list`, params })
}

export const qmsList = async (params) => {
  return await request.get({
    url: `/qms/quality-inspection/get-simple-list`,
    params,
    neverLoading: true
  })
}

//根据部门id 获取部门负责人
export const getDeptLeader = async (params) => {
  return await request.get({ url: `/system/dept/get-leader-id-list`, params })
}

//校验产品是否有效
export const checkValidSku = async (ids) => {
  return await request.get({ url: `/pms/base-sku/validate-sku?codeList=${ids}` })
}

export const checkValidVender = async (ids) => {
  return await request.get({ url: `/scm/vender/validate-vender?codeList=${ids}` })
}

//根据code查询产品详情
export const getSkuInfoByCode = async (code) => {
  return await request.get({ url: `pms/sku/get-simple-detail?skuCode=${code}` })
}
