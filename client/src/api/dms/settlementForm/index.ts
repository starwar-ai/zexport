import request from '@/config/axios'
// 查询分页
export const getSettlementFormPage = async (params) => {
  return await request.get({ url: `/dms/settlement-form/page`, params })
}

export const getSettlementFormInfo = async (params) => {
  return await request.get({ url: `/dms/settlement-form/detail`, params })
}

export const updateSettlement = async (data) => {
  return await request.put({ url: `/dms/settlement-form/update`, data })
}

// export const delSettlement = async (id) => {
//   return await request.delete({ url: `dms/settlement-form/delete?id=${id}` })
// }
export const delSettlement = async (ids) => {
  return await request.delete({ url: `/dms/settlement-form/batch-delete?idList=${ids}` })
}

// 导出Excel
export const exportSettlementForm = async (params) => {
  return await request.download({
    url: `/dms/settlement-form/export-excel`,
    params,
    neverLoading: true
  })
}
