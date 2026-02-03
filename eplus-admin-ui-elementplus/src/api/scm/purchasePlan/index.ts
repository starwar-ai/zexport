import request from '@/config/axios'

export interface purchasePlanVO {
  id: number
  ver: number
  code: string
  name: string
  nameEng: string
  nameShort: string
  provinceId: number
  cityId: number
  address: string
  licenseNo: string
  phone: string
  buyerId: number
  pocName: string
  pocTypes: string
  pocMobile: number
  abroadFlag: number
  countryId: number
  currency: string
  taxRate: number
  taxType: string
  paymentTermId: number
  auditStatus: string
  bank: string
  bankAccount: string
  creator: string
  creatorName: string
  createTime: Date
  fileList: []
  bankaccountSaveReqList: [
    {
      bank: undefined
      bankAccount: undefined
      defaultFlag: undefined
    }
  ]
  managerSaveReqList: [
    {
      manager_id: undefined
      defaultFlag: undefined
    }
  ]
  pocSaveReqList: [
    {
      name: undefined
      email: undefined
      mobile: undefined
      pocTypes: undefined
      address: undefined
      defaultFlag: undefined
      taskId: string
      createUser: string
      auditUser: string
      auditStatus: number
    }
  ]
  auditInfo: {
    taskId: string
    createUser: string
    auditUser: string
    auditStatus: number
  }
  submitFlag: number
}

// 查询采购计划信息分页
export const getPurchasePlanPage = async (params) => {
  return await request.get({ url: `/scm/purchase-plan/page`, params })
}
// 查询采购计划信息分页
export const getPurchasePlanPageBoard = async (params) => {
  return await request.get({ url: `/scm/purchase-plan/page-board`, params })
}
//获取客户列表
export const getCustSimpleList = async (params) => {
  return await request.get({ url: `/crm/cust/get-simple-list`, params })
}
// 查询采购计划信息详情
export const getPurchasePlan = async (params) => {
  return await request.get({ url: `/scm/purchase-plan/detail`, params })
}
//查询采购计划详情关联单据数
export const getPlanRelateNum = async (params) => {
  return await request.get({ url: `/scm/purchase-plan/related-num`, params })
}

//审核模块的获取详情
export const getAuditpurchasePlan = async (params) => {
  return await request.get({ url: '/scm/purchase-plan/audit-detail', params })
}

// 新增采购计划信息
export const createPurchasePlan = async (data: purchasePlanVO) => {
  return await request.post({ url: `/scm/purchase-plan/create`, data })
}

// 修改采购计划信息
export const updatePurchasePlan = async (data: purchasePlanVO) => {
  return await request.put({ url: `/scm/purchase-plan/update`, data })
}
//转采购合同post
export const purchaseToContract = async (data: purchasePlanVO) => {
  return await request.post({ url: `/scm/purchase-plan/to-contract-save`, data })
}

// 作废
export const finishPurchasePlan = async (data) => {
  return await request.put({ url: `/scm/purchase-plan/batch-finish?planIdList=${data}` })
}

// 删除采购计划信息
export const deletePurchasePlan = async (id: number) => {
  return await request.delete({ url: `/scm/purchase-plan/delete?id=` + id })
}

// 导出采购计划信息 Excel
export const exportPurchasePlan = async (params) => {
  return await request.download({ url: `/scm/purchase-plan/export-excel`, params })
}

//查询税率信息
export const getTaxRate = async (params?) => {
  return await request.get({ url: `/infra/rate/get-simple-list`, params })
}

//应付采购计划
export const getPayablePurchasePlan = async (params?) => {
  return await request.get({ url: `/scm/purchase-plan/get-simple-list`, params })
}

//采购计划提交
export const submitPurchasePlan = async (id) => {
  return await request.put({ url: `/scm/purchase-plan/submit?paymentId=${id}` })
}
//获取产品精简列表
export const getSkuSimpleList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-sku`, params })
}
//获取自营产品精简列表
export const getSkuSimpleOwnList = async (params) => {
  return await request.get({ url: `/pms/sku/simple-own-sku`, params })
}
export const toFormalPurchasePlan = async (data) => {
  return await request.put({ url: `/scm/purchase-plan/convert`, data })
}
//获取采购主体
export const getcompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-simple-list` })
}
//获取默认采购主体
export const getProcessedCompanySimpleList = async () => {
  return await request.get({ url: `/system/company/get-process-list` })
}
//获取转采购合同列表
export const getTocontractList = async (ids) => {
  return await request.get({ url: `/scm/purchase-plan/to-contract-list?planItemIdList=${ids}` })
}
//根据skucode获取供应商报价详情
// export const getVenderQuoteList = async (code) => {
//   return await request.get({ url: `/scm/quote-item/list-by-skucode?skuCodeList=${code}` })
// }
//根据skuid获取供应商报价详情
export const getVenderQuoteList = async (id) => {
  return await request.get({ url: `/scm/quote-item/list-by-skuid?skuIdList=${id}` })
}
//转采购合同
export const toContract = async (list) => {
  return await request.post({ url: `/scm/purchase-plan/to-contract?planItemIdList=${list}` })
}
//判断是否可以修改客户
export const ifChangeCust = async (data) => {
  return await request.post({ url: `/pms/sku/check-cust`, data })
}

// 计划明细编辑
export const planItemUpdate = async (data) => {
  return await request.put({ url: `/scm/purchase-plan-item/update`, data })
}

export const getProductMixdetail = async (params) => {
  return await request.get({ url: `/scm/purchase-plan/productMixdetail`, params })
}

// 计划反审核
export const planRevertAudit = async (id) => {
  return await request.put({ url: `/scm/purchase-plan/anti-audit?id=${id}` })
}

export const createQuote = async (data) => {
  return await request.post({ url: `/scm/quote-item/create`, data })
}
