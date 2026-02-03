/**
 * 图片加载 Hook
 * @author 波波
 * @date 2024-01-01
 */

import { computed, onMounted, onUnmounted, ref, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import { preloadImage } from '@/utils/imagePreloader'

export interface ImageLoaderOptions {
  /** 重试次数 */
  retryCount?: number
  /** 加载超时时间（毫秒） */
  timeout?: number
  /** 是否自动重试 */
  autoRetry?: boolean
  /** 重试延迟（毫秒） */
  retryDelay?: number
  /** 是否显示加载状态 */
  showLoading?: boolean
  /** 是否预加载 */
  preload?: boolean
}

export interface ImageLoadState {
  /** 是否正在加载 */
  loading: boolean
  /** 是否加载成功 */
  loaded: boolean
  /** 是否加载失败 */
  error: boolean
  /** 当前重试次数 */
  retryCount: number
  /** 图片URL */
  url: string
  /** 加载耗时（毫秒） */
  duration: number
}

export function useImageLoader(
  imageUrl: string | (() => string),
  options: ImageLoaderOptions = {}
) {
  const {
    retryCount = 3,
    timeout = 10000,
    autoRetry = true,
    retryDelay = 1000,
    showLoading = true,
    preload = true
  } = options

  // 响应式状态
  const loading = ref(false)
  const loaded = ref(false)
  const error = ref(false)
  const currentRetryCount = ref(0)
  const currentUrl = ref('')
  const duration = ref(0)
  const imageLoadTimeout = ref<NodeJS.Timeout | null>(null)

  // 计算属性
  const status = computed(() => {
    if (error.value) return 'error'
    if (loaded.value) return 'loaded'
    if (loading.value) return 'loading'
    return 'idle'
  })

  const hasValidUrl = computed(() => {
    const url = typeof imageUrl === 'function' ? imageUrl() : imageUrl
    return !!url && url.trim() !== ''
  })

  // 工具函数
  const getCurrentUrl = (): string => {
    return typeof imageUrl === 'function' ? imageUrl() : imageUrl
  }

  const buildImageUrl = (path: string): string => {
    if (!path) return ''

    // 如果是完整的 URL，直接返回
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path
    }

    // 如果是 base64，直接返回
    if (path.startsWith('data:')) {
      return path
    }

    // 拼接基础 URL
    const baseUrl = import.meta.env.VITE_BASE_URL || ''
    const apiPath = '/admin-api'

    // 确保路径以 / 开头
    const normalizedPath = path.startsWith('/') ? path : `/${path}`

    return `${baseUrl}${apiPath}${normalizedPath}`
  }

  // 图片加载处理
  const handleImageLoad = (loadDuration: number) => {
    loading.value = false
    error.value = false
    loaded.value = true
    currentRetryCount.value = 0
    duration.value = loadDuration

    if (imageLoadTimeout.value) {
      clearTimeout(imageLoadTimeout.value)
      imageLoadTimeout.value = null
    }
  }

  const handleImageError = () => {
    loading.value = false
    error.value = true
    loaded.value = false

    if (imageLoadTimeout.value) {
      clearTimeout(imageLoadTimeout.value)
      imageLoadTimeout.value = null
    }

    // 自动重试
    if (autoRetry && currentRetryCount.value < retryCount) {
      currentRetryCount.value++
      setTimeout(() => {
        loadImage()
      }, retryDelay * currentRetryCount.value) // 递增延迟重试
    }
  }

  // 加载图片
  const loadImage = async (url?: string): Promise<boolean> => {
    const targetUrl = url || getCurrentUrl()

    if (!targetUrl) {
      error.value = true
      return false
    }

    // 重置状态
    loading.value = true
    error.value = false
    loaded.value = false
    currentUrl.value = targetUrl

    try {
      const startTime = Date.now()

      if (preload) {
        // 使用预加载器
        const result = await preloadImage(targetUrl)
        const loadDuration = Date.now() - startTime

        if (result.success) {
          handleImageLoad(loadDuration)
          return true
        } else {
          handleImageError()
          return false
        }
      } else {
        // 直接加载
        return new Promise((resolve) => {
          const img = new Image()

          // 设置加载超时
          imageLoadTimeout.value = setTimeout(() => {
            if (loading.value) {
              loading.value = false
              error.value = true
              ElMessage.warning('图片加载超时')
              resolve(false)
            }
          }, timeout)

          img.onload = () => {
            const loadDuration = Date.now() - startTime
            handleImageLoad(loadDuration)
            resolve(true)
          }

          img.onerror = () => {
            handleImageError()
            resolve(false)
          }

          img.src = targetUrl
        })
      }
    } catch (err) {
      handleImageError()
      return false
    }
  }

  // 重试加载
  const retry = () => {
    currentRetryCount.value = 0
    return loadImage()
  }

  // 强制重新加载
  const reload = () => {
    const url = getCurrentUrl()
    if (url) {
      // 添加时间戳防止缓存
      const separator = url.includes('?') ? '&' : '?'
      const timestamp = Date.now()
      const reloadUrl = `${url}${separator}_t=${timestamp}`
      return loadImage(reloadUrl)
    }
    return Promise.resolve(false)
  }

  // 监听 URL 变化
  watchEffect(() => {
    const newUrl = getCurrentUrl()

    if (newUrl !== currentUrl.value) {
      currentUrl.value = newUrl
      currentRetryCount.value = 0

      if (newUrl && hasValidUrl.value) {
        loadImage()
      } else {
        // 无图片时重置所有状态
        loading.value = false
        error.value = false
        loaded.value = false
        duration.value = 0
      }
    }
  })

  // 生命周期
  onMounted(() => {
    // 组件挂载后开始加载图片
    if (hasValidUrl.value) {
      const url = getCurrentUrl()
      if (url) {
        loadImage()
      }
    }
  })

  onUnmounted(() => {
    // 清理定时器
    if (imageLoadTimeout.value) {
      clearTimeout(imageLoadTimeout.value)
      imageLoadTimeout.value = null
    }
  })

  return {
    // 状态
    loading,
    loaded,
    error,
    status,
    currentRetryCount,
    currentUrl,
    duration,
    hasValidUrl,

    // 方法
    loadImage,
    retry,
    reload
  }
}
