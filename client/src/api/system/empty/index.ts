import request from '@/config/axios'

export const getSystemEmpty = async () => {
  return await request.get({ url: '/system/empty' })
}
