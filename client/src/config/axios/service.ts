import axios, { AxiosError, AxiosInstance, AxiosRequestHeaders, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import Loading from './loading'
import qs from 'qs'
import { config } from '@/config/axios/config'
import { getAccessToken } from '@/utils/auth'
import errorCode from './errorCode'
import { CONTENT_TYPE, HTTP_STATUS, IGNORE_ERROR_MSGS, WHITE_LIST_URLS, NO_LOADING_URLS } from './constants'
import { ErrorHandler } from './errorHandler'
import { requestQueueManager } from './requestQueue'
import { RequestConfig } from '@/types/axios'

const { base_url, request_timeout } = config
export const isRelogin = { show: false }
// 错误处理器实例
const errorHandler = new ErrorHandler()

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: base_url, // api 的 base_url
  timeout: request_timeout, // 请求超时时间
  withCredentials: false // 禁用 Cookie 等信息
})

/**
 * 检查请求是否需要 token
 */
const shouldSkipToken = (config: RequestConfig): boolean => {
  // 如果配置中明确设置了 isToken 为 false，则跳过
  if ((config.headers as any).isToken === false) {
    return true
  }

  // 检查是否在白名单中
  if (config.url) {
    return WHITE_LIST_URLS.some((whiteUrl) => config.url!.includes(whiteUrl))
  }

  return false
}

/**
 * 判断请求是否需要显示 Loading
 * @param config - axios 请求配置
 * @returns true - 显示 loading，false - 不显示 loading
 */
const shouldShowLoading = (config: RequestConfig): boolean => {
  // 1. 优先检查 showLoading 配置（手动控制，优先级最高）
  if (config.showLoading !== undefined) {
    return config.showLoading
  }

  // 2. 兼容旧的 neverLoading 配置
  if (config.neverLoading !== undefined) {
    return !config.neverLoading
  }

  // 3. 检查 URL 是否匹配不需要 loading 的模式
  if (config.url) {
    const shouldSkip = NO_LOADING_URLS.some(pattern => config.url!.includes(pattern))
    if (shouldSkip) {
      return false
    }
  }

  // 4. 默认显示 loading
  return true
}

/**
 * 构建 GET 请求的查询字符串
 */
const buildQueryString = (params: Record<string, any>): string => {
  const queryParts: string[] = []

  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null) continue

    if (typeof value === 'object') {
      // 处理对象类型的参数
      for (const [subKey, subValue] of Object.entries(value)) {
        const paramKey = `${key}[${subKey}]`
        queryParts.push(`${encodeURIComponent(paramKey)}=${encodeURIComponent(subValue as string)}`)
      }
    } else {
      queryParts.push(`${key}=${encodeURIComponent(value)}`)
    }
  }

  return queryParts.join('&')
}

// request拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 设置 token
    if (getAccessToken() && !shouldSkipToken(config)) {
      ;(config as Recordable).headers.Authorization = 'Bearer ' + getAccessToken()
    }

    // 添加 loading 效果
    if (shouldShowLoading(config)) {
      Loading.start()
    }


    // 处理 POST 请求的表单数据
    const data = config.data || false
    if (
      config.method?.toUpperCase() === 'POST' &&
      (config.headers as AxiosRequestHeaders)['Content-Type'] === CONTENT_TYPE.FORM_URLENCODED
    ) {
      config.data = qs.stringify(data)
    }

    // 处理 GET 请求参数
    const params = config.params
    if (config.method?.toUpperCase() === 'GET' && params && Object.keys(params).length > 0) {
      const queryString = buildQueryString(params)
      config.url = `${config.url}?${queryString}`
      config.params = {}
    }

    return config
  },
  (error: AxiosError) => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  async (response: AxiosResponse<any>) => {
    const { data, config } = response

    try {
      // 检查响应数据是否存在
      if (!data) {
        throw new Error('响应数据为空')
      }

      // 处理二进制响应（blob 或 arraybuffer）
      if (
        response.request.responseType === 'blob' ||
        response.request.responseType === 'arraybuffer'
      ) {
        const result = await errorHandler.handleBlobResponse(response)
        return result
      }

      // 关闭 loading
      if (shouldShowLoading(config)) {
        Loading.close()
      }

      // 获取响应状态码
      const code = data.code ?? HTTP_STATUS.SERVER_ERROR
      const message = data.msg || errorCode[code] || errorCode['default']

      // 检查是否为需要忽略的错误信息
      if (IGNORE_ERROR_MSGS.includes(message)) {
        return Promise.reject(message)
      }

      // 处理 401 未授权错误
      if (code === HTTP_STATUS.UNAUTHORIZED) {
        return await requestQueueManager.handleUnauthorized(config, service)
      }

      // 处理其他错误状态码
      if (code !== HTTP_STATUS.SUCCESS) {
        return errorHandler.handleResponseError(response)
      }

      // 成功响应
      return data
    } catch (error) {
      // 确保 loading 被关闭
      if (shouldShowLoading(config)) {
        Loading.close()
      }
      throw error
    } finally {
      // 关闭 loading
      if (shouldShowLoading(config)) {
        Loading.close()
      }
    }
  },
  (error: AxiosError) => {
    Loading.close()
    return errorHandler.handleRequestError(error)
  }
)

export { service }
