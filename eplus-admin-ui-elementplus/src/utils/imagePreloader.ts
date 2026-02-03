/**
 * 图片预加载工具类
 * @author 波波
 * @date 2024-01-01
 */

export interface ImagePreloadOptions {
  /** 最大并发加载数量 */
  maxConcurrent?: number
  /** 加载超时时间（毫秒） */
  timeout?: number
  /** 是否启用缓存 */
  enableCache?: boolean
  /** 缓存大小限制 */
  maxCacheSize?: number
}

export interface ImageLoadResult {
  /** 是否加载成功 */
  success: boolean
  /** 图片元素 */
  image?: HTMLImageElement
  /** 错误信息 */
  error?: string
  /** 加载耗时（毫秒） */
  duration: number
}

export class ImagePreloader {
  private cache = new Map<string, HTMLImageElement>()
  private loadingQueue: Array<{
    url: string
    resolve: (result: ImageLoadResult) => void
    reject: (error: Error) => void
  }> = []
  private currentLoading = 0
  private options: Required<ImagePreloadOptions>

  constructor(options: ImagePreloadOptions = {}) {
    this.options = {
      maxConcurrent: options.maxConcurrent || 3,
      timeout: options.timeout || 10000,
      enableCache: options.enableCache !== false,
      maxCacheSize: options.maxCacheSize || 100
    }
  }

  /**
   * 预加载单张图片
   * @param url 图片URL
   * @param priority 是否优先加载
   * @returns Promise<ImageLoadResult>
   */
  preloadImage(url: string, priority = false): Promise<ImageLoadResult> {
    return new Promise((resolve, reject) => {
      // 检查缓存
      if (this.options.enableCache && this.cache.has(url)) {
        const cachedImage = this.cache.get(url)!
        resolve({
          success: true,
          image: cachedImage,
          duration: 0
        })
        return
      }

      // 添加到加载队列
      const queueItem = { url, resolve, reject }
      if (priority) {
        this.loadingQueue.unshift(queueItem)
      } else {
        this.loadingQueue.push(queueItem)
      }

      this.processQueue()
    })
  }

  /**
   * 预加载多张图片
   * @param urls 图片URL数组
   * @param priority 是否优先加载
   * @returns Promise<ImageLoadResult[]>
   */
  preloadImages(urls: string[], priority = false): Promise<ImageLoadResult[]> {
    const promises = urls.map((url) => this.preloadImage(url, priority))
    return Promise.all(promises)
  }

  /**
   * 处理加载队列
   */
  private async processQueue(): Promise<void> {
    if (this.currentLoading >= this.options.maxConcurrent || this.loadingQueue.length === 0) {
      return
    }

    this.currentLoading++
    const { url, resolve, reject } = this.loadingQueue.shift()!

    try {
      const result = await this.loadSingleImage(url)
      resolve(result)
    } catch (error) {
      reject(error as Error)
    } finally {
      this.currentLoading--
      this.processQueue()
    }
  }

  /**
   * 加载单张图片
   * @param url 图片URL
   * @returns Promise<ImageLoadResult>
   */
  private loadSingleImage(url: string): Promise<ImageLoadResult> {
    return new Promise((resolve, reject) => {
      const startTime = Date.now()
      const img = new Image()
      const timeoutId = setTimeout(() => {
        img.src = ''
        reject(new Error('图片加载超时'))
      }, this.options.timeout)

      img.onload = () => {
        clearTimeout(timeoutId)
        const duration = Date.now() - startTime

        // 添加到缓存
        if (this.options.enableCache) {
          this.addToCache(url, img)
        }

        resolve({
          success: true,
          image: img,
          duration
        })
      }

      img.onerror = () => {
        clearTimeout(timeoutId)
        const duration = Date.now() - startTime
        resolve({
          success: false,
          error: '图片加载失败',
          duration
        })
      }

      img.src = url
    })
  }

  /**
   * 添加到缓存
   * @param url 图片URL
   * @param image 图片元素
   */
  private addToCache(url: string, image: HTMLImageElement): void {
    // 检查缓存大小限制
    if (this.cache.size >= this.options.maxCacheSize) {
      // 删除第一个缓存项
      const firstKey = this.cache.keys().next().value
      if (firstKey) {
        this.cache.delete(firstKey)
      }
    }

    this.cache.set(url, image)
  }

  /**
   * 获取缓存的图片
   * @param url 图片URL
   * @returns HTMLImageElement | undefined
   */
  getCachedImage(url: string): HTMLImageElement | undefined {
    return this.cache.get(url)
  }

  /**
   * 检查图片是否已缓存
   * @param url 图片URL
   * @returns boolean
   */
  isCached(url: string): boolean {
    return this.cache.has(url)
  }

  /**
   * 清理缓存
   * @param url 指定URL，如果不传则清理所有缓存
   */
  clearCache(url?: string): void {
    if (url) {
      this.cache.delete(url)
    } else {
      this.cache.clear()
    }
  }

  /**
   * 获取缓存统计信息
   */
  getCacheStats() {
    return {
      size: this.cache.size,
      maxSize: this.options.maxCacheSize,
      loadingCount: this.currentLoading,
      queueLength: this.loadingQueue.length
    }
  }

  /**
   * 预加载当前页面的图片
   * @param selector 图片选择器，默认为所有 img 标签
   * @returns Promise<ImageLoadResult[]>
   */
  preloadPageImages(selector = 'img'): Promise<ImageLoadResult[]> {
    const images = document.querySelectorAll<HTMLImageElement>(selector)
    const urls = Array.from(images)
      .map((img) => img.src)
      .filter((url) => url && !url.startsWith('data:'))
      .filter((url) => !this.isCached(url))

    return this.preloadImages(urls)
  }
}

// 创建全局实例
export const globalImagePreloader = new ImagePreloader()

// 导出便捷函数
export const preloadImage = (url: string, priority = false) =>
  globalImagePreloader.preloadImage(url, priority)

export const preloadImages = (urls: string[], priority = false) =>
  globalImagePreloader.preloadImages(urls, priority)

export const preloadPageImages = (selector?: string) =>
  globalImagePreloader.preloadPageImages(selector)
