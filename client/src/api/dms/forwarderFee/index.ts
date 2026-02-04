import request from '@/config/axios'
// 查询分页
export const getForwarderFeeApplyPage = async (params) => {
  return await request.get({ url: `/dms/forwarder-fee/apply-page`, params })
}
