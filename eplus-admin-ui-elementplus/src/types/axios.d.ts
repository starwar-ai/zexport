import type { AxiosRequestConfig } from 'axios'

/**
 * API 响应基础结构
 */
export interface ApiResponse<T = any> {
  /** 响应状态码 */
  code: number
  /** 响应数据 */
  data: T
  /** 响应消息 */
  message: string
}

/**
 * 分页响应结构
 */
export interface PageResponse<T = any> {
  /** 数据列表 */
  list: T[]
  /** 总条数 */
  total: number
}

/**
 * 刷新令牌响应
 */
export interface RefreshTokenResponse {
  access_token: string
  refresh_token: string
  expires_in: number
}

/**
 * 请求配置扩展
 */
export interface RequestConfig extends AxiosRequestConfig {
  /** 是否跳过 token */
  skipToken?: boolean
  /** 是否显示 loading（优先级最高）*/
  showLoading?: boolean
  /** @deprecated 使用 showLoading 代替 */
  neverLoading?: boolean
  /** 是否跳过错误提示 */
  skipErrorHandler?: boolean
}

/**
 * 请求队列回调函数类型
 */
export type RequestQueueCallback = () => void

/**
 * 错误响应结构
 */
export interface ErrorResponse {
  code: number
  message: string
  data?: any
}

/**
 * Loading 管理器接口
 */
export interface LoadingManager {
  start(): void
  close(): void
  done(): void
}



