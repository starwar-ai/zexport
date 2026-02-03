import request from '@/config/axios'

// 获取最新版本号
export const getLastVersion = () => {
  return request.get({ url: '/infra/version/get-last' })
} 
// 获取版本分页
export const getVersionPage = (params: any) => {
  return request.get({ url: '/infra/version/page', params })
}

