import request from '@/config/axios'

export interface CustVO {
  // id: number
  // ver: number
  name: string
  shortname: string
  code: string
  countryId: number
  regionId: number
  homepage: string
  email: string
  customerTypes: []
  stageType: number
  currency: number
  paymentMethod: number
  // transportType: number
  address: string
  addressExpress: string
  addressGoods: string
  phone: string
  bank: string
  bankAccount: string
  pocName: string
  pocType: boolean
  pocMobile: string
  abroadFlag: boolean
  sourceType: number
  creditFlag: boolean
  creditLimit: string
  zxbquotaFlag: boolean
  settlementTermType: number
  invoiceHeader: string
  taxRate: number
  markId: number
  agentFlag: boolean
  addressShipping: string
  finacial_status: number
  pocList: [
    {
      name: string
      email: string
      mobile: string
      pocPosts: undefined
      deptId: number
      address: string
      defaultFlag: number
    }
  ]
  bankaccountSaveReqList: [
    {
      bank: string
      bankAccount: string
    }
  ]
  auditInfo: {
    taskId: string
    createUser: string
    auditUser: string
    auditStatus: number
  }
}
export const formDataInitObj = {
  name: '',
  shortname: '',
  phone: '',
  email: '',
  address: '',
  homepage: '',
  countryId: '',
  regionName: '',
  agentFlag: '',
  stageType: '',
  sourceType: '',
  managerList: [],
  customerTypes: [],
  creditFlag: 0,
  creditLimit: '',
  remark: '',
  currency: '',
  settlementTermType: '',
  settlementList: '',
  //联系人和银行账户默认有一条数据
  pocList: [],
  bankaccountList: [],
  annex: [],
  pictureList: []
}
export const formDataInit = (args?) => {
  args &&
    Object.keys(args).forEach((key) => {
      formDataInitObj[key] = args[key]
    })
  return formDataInitObj
}

// 查询客户资料分页
export const getCustPage = async (params) => {
  return await request.get({ url: `/crm/cust/page`, params })
}

// 查询客户资料分页
export const custDuplicateCheck = async (params) => {
  return await request.get({ url: `/crm/cust/cust-duplicate-check`, params })
}

// 查询客户资料详情
export const getCustomer = async (params) => {
  return await request.get({ url: '/crm/cust/detail', params })
}

//审核模块的获取详情
export const getAuditCustomer = async (params) => {
  return await request.get({ url: '/crm/cust/audit-detail', params })
}

// 新增客户资料
export const createCust = async (data: CustVO) => {
  return await request.post({ url: `/crm/cust/create`, data })
}

//提交审核客户
export const submitCust = async (id) => {
  return await request.put({ url: `/crm/cust/submit?custId=${id}` })
}
//启用
export const enableCust = async (id) => {
  return await request.put({ url: `/crm/cust/enable?custId=${id}` })
}
//禁用
export const disEnableCust = async (id) => {
  return await request.put({ url: `/crm/cust/disable?custId=${id}` })
}
// 修改客户资料
export const updateCust = async (data: CustVO) => {
  return await request.put({ url: `/crm/cust/update`, data })
}

// 变更客户资料
export const changeCust = async (data: CustVO) => {
  return await request.put({ url: `/crm/cust/change`, data })
}

// 删除客户资料
export const deleteCust = async (id: number) => {
  return await request.delete({ url: `/crm/cust/delete?id=` + id })
}

export const manDeleteCust = async (id: number) => {
  return await request.delete({ url: `/crm/cust/manager-delete?id=` + id })
}

// 导出客户资料 Excel
export const exportCust = async (params) => {
  return await request.download({ url: `/crm/cust/export-excel`, params })
}

//收款方式
export const getSettle = async () => {
  return await request.get({ url: `/settlement/settlement/get-simple-list ` })
}

//获取客户编号
export const getCustCode = async () => {
  return await request.get({ url: `/crm/cust/code` })
}

//转正式客户
export const putConvert = async (data: CustVO) => {
  return await request.put({ url: `crm/cust/convert`, data })
}
//搜索相似名称
export const findSimilarNames = async (params) => {
  return await request.get({ url: `/crm/cust/find-similar-names`, params })
}

// 获得变更客户列表
export const getCustChangePage = async (params) => {
  return await request.get({ url: `/crm/cust/change-page`, params })
}

// 获得变更客户详情
export const getCustChangeDetail = async (params) => {
  return await request.get({ url: `/crm/cust/change-detail`, params })
}

export const getCustAuditChangeDetail = async (params) => {
  return await request.get({ url: `/crm/cust/audit-change-detail`, params })
}

// 更新变更客户资料
export const changeUpdate = async (data) => {
  return await request.put({ url: `/crm/cust/change-update`, data })
}

// 提交变更任务
export const changeSubmit = async (id) => {
  return await request.put({ url: `/crm/cust/change-submit?custId=${id}` })
}

// 删除变更客户资料
export const deleteChangeCust = async (id: number) => {
  return await request.delete({ url: `/crm/cust/change-delete?id=` + id })
}

// 获得变更影响列表"
export const getCustChangeEffect = async (data) => {
  return await request.post({ url: `/crm/cust/change-effect`, data })
}
//反审核
export const revertAudit = async (id) => {
  return await request.put({ url: `/crm/cust/anti-audit?custId=${id}` })
}
//查询内部公司银行信息
export const companyBanklist = async () => {
  return await request.get({ url: `/system/company/bank-list` })
}

export const getCustCategoryTree = async () => {
  return await request.get({ url: `/crm/category/get-simple-list` })
}
