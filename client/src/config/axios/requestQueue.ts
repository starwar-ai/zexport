import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import { t } from '@/hooks/web/useI18n'
import { getAccessToken, getRefreshToken, removeToken, setToken } from '@/utils/auth'
import { resetRouter } from '@/router'
import { useCache } from '@/hooks/web/useCache'
import { RELOGIN_STATUS } from './constants'
import { ErrorHandler } from './errorHandler'
import type { RequestQueueCallback } from '@/types/axios'

/**
 * 请求队列管理器
 */
export class RequestQueueManager {
  private requestList: RequestQueueCallback[] = []
  private isRefreshing = false
  private errorHandler: ErrorHandler

  constructor() {
    this.errorHandler = new ErrorHandler()
  }

  /**
   * 添加请求到队列
   */
  addToQueue(callback: RequestQueueCallback): void {
    this.requestList.push(callback)
  }

  /**
   * 清空请求队列
   */
  clearQueue(): void {
    this.requestList = []
  }

  /**
   * 执行队列中的所有请求
   */
  executeQueue(): void {
    this.requestList.forEach(callback => callback())
    this.clearQueue()
  }

  /**
   * 处理 401 未授权错误
   */
  async handleUnauthorized(
    config: InternalAxiosRequestConfig,
    service: AxiosInstance
  ): Promise<any> {
    if (this.isRefreshing) {
      // 如果正在刷新，将请求添加到队列中
      return new Promise(resolve => {
        this.addToQueue(() => {
          config.headers!.Authorization = 'Bearer ' + getAccessToken()
          resolve(service(config))
        })
      })
    }

    this.isRefreshing = true

    // 1. 检查是否有刷新令牌
    if (!getRefreshToken()) {
      return this.handleRelogin()
    }

    // 2. 尝试刷新访问令牌
    try {
      const refreshResponse = await this.refreshToken()
      const newToken = refreshResponse.data.data

      // 更新令牌
      setToken(newToken)

      // 更新当前请求的 Authorization 头
      config.headers!.Authorization = 'Bearer ' + getAccessToken()

      // 执行队列中的请求
      this.executeQueue()

      // 返回当前请求的结果
      return service(config)
    } catch (error) {
      // 刷新失败，执行队列中的请求（不包含当前请求）
      this.executeQueue()
      // 显示重新登录提示
      return this.handleRelogin()
    } finally {
      this.isRefreshing = false
    }
  }

  /**
   * 刷新访问令牌
   */
  private async refreshToken(): Promise<any> {
    const { config } = await import('./config')
    return axios.post(`${config.base_url}/system/auth/refresh-token?refreshToken=${getRefreshToken()}`)
  }

  /**
   * 处理重新登录
   */
  private handleRelogin(): Promise<never> {
    if (!RELOGIN_STATUS.show) {
      // 如果已经在重新登录页面则不进行弹窗提示
      if (window.location.href.includes('login?redirect=')) {
        return Promise.reject(t('sys.api.timeoutMessage'))
      }

      RELOGIN_STATUS.show = true

      this.errorHandler.showReloginConfirm().then(() => {
        const { wsCache, wsCacheSession } = useCache()

        // 重置路由
        resetRouter()

        // 清空缓存
        wsCache.clear()
        wsCacheSession.clear()

        // 移除令牌
        removeToken()

        // 重置状态
        RELOGIN_STATUS.show = false

        // 刷新页面
        window.location.href = window.location.href
      })
    }

    return Promise.reject(t('sys.api.timeoutMessage'))
  }

  /**
   * 检查是否正在刷新令牌
   */
  get isRefreshingToken(): boolean {
    return this.isRefreshing
  }
}

// 创建单例实例
export const requestQueueManager = new RequestQueueManager()



