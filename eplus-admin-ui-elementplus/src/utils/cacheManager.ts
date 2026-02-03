/**
 * 缓存管理工具
 * @author 波波
 * @date 2024-01-01
 */

/**
 * 强制刷新图片缓存
 * @param url 原始图片URL
 * @returns 带有缓存破坏参数的新URL
 */
export function bustImageCache(url: string): string {
  if (!url) return url

  const timestamp = Date.now()
  const random = Math.random().toString(36).substr(2, 9)
  const separator = url.includes('?') ? '&' : '?'

  return `${url}${separator}_t=${timestamp}&_r=${random}`
}

/**
 * 清除特定URL的浏览器缓存
 * @param url 图片URL
 */
export async function clearImageCache(url: string): Promise<void> {
  try {
    // 尝试使用 Cache API 清除缓存
    if ('caches' in window) {
      const cacheNames = await caches.keys()
      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        await cache.delete(url)
        console.log(`清除缓存: ${cacheName} - ${url}`)
      }
    }

    // 清除 Service Worker 缓存
    if ('serviceWorker' in navigator && navigator.serviceWorker.controller) {
      navigator.serviceWorker.controller.postMessage({
        type: 'CLEAR_CACHE',
        url: url
      })
    }
  } catch (error) {
    console.warn('清除缓存失败:', error)
  }
}

/**
 * 强制重新加载图片
 * @param url 图片URL
 * @returns Promise<boolean> 是否加载成功
 */
export function forceReloadImage(url: string): Promise<boolean> {
  return new Promise((resolve) => {
    console.log('🔄 强制重新加载图片:', url)

    // 创建一个新的图片元素
    const img = new Image()
    const startTime = Date.now()

    // 设置超时
    const timeoutId = setTimeout(() => {
      console.warn('⏰ 强制重新加载超时:', url)
      img.src = ''
      resolve(false)
    }, 15000) // 15秒超时

    img.onload = () => {
      clearTimeout(timeoutId)
      const duration = Date.now() - startTime
      console.log(`✅ 强制重新加载成功: ${url} (耗时: ${duration}ms)`)
      resolve(true)
    }

    img.onerror = (error) => {
      clearTimeout(timeoutId)
      const duration = Date.now() - startTime
      console.error(`❌ 强制重新加载失败: ${url} (耗时: ${duration}ms)`, error)
      resolve(false)
    }

    // 使用带缓存破坏参数的URL
    const bustUrl = bustImageCache(url)
    console.log('🔗 使用缓存破坏URL:', bustUrl)

    // 设置跨域属性以避免CORS问题
    img.crossOrigin = 'anonymous'
    img.src = bustUrl
  })
}

/**
 * 批量强制重新加载图片
 * @param urls 图片URL数组
 * @param concurrency 并发数
 */
export async function batchForceReload(
  urls: string[],
  concurrency = 3
): Promise<Array<{ url: string; success: boolean }>> {
  console.group(`🔄 批量强制重新加载 ${urls.length} 张图片`)

  const results: Array<{ url: string; success: boolean }> = []

  // 分批处理
  for (let i = 0; i < urls.length; i += concurrency) {
    const batch = urls.slice(i, i + concurrency)
    console.log(`处理第 ${Math.floor(i / concurrency) + 1} 批，共 ${batch.length} 张图片`)

    const batchResults = await Promise.all(
      batch.map(async (url) => {
        const success = await forceReloadImage(url)
        return { url, success }
      })
    )

    results.push(...batchResults)

    // 批次间延迟，避免服务器压力过大
    if (i + concurrency < urls.length) {
      await new Promise((resolve) => setTimeout(resolve, 1000))
    }
  }

  const successCount = results.filter((r) => r.success).length
  const failCount = results.filter((r) => !r.success).length

  console.log(`📊 批量重新加载结果:`)
  console.log(`✅ 成功: ${successCount}`)
  console.log(`❌ 失败: ${failCount}`)
  console.log(`📈 成功率: ${((successCount / results.length) * 100).toFixed(1)}%`)

  console.groupEnd()
  return results
}

/**
 * 清除所有图片缓存
 */
export async function clearAllImageCache(): Promise<void> {
  try {
    console.log('🧹 开始清除所有图片缓存...')

    if ('caches' in window) {
      const cacheNames = await caches.keys()

      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        const keys = await cache.keys()

        for (const request of keys) {
          const url = request.url
          if (url.match(/\.(jpg|jpeg|png|gif|webp|svg)(\?|$)/i)) {
            await cache.delete(request)
            console.log(`清除图片缓存: ${url}`)
          }
        }
      }
    }

    console.log('✅ 图片缓存清除完成')
  } catch (error) {
    console.error('❌ 清除图片缓存失败:', error)
  }
}

/**
 * 检测图片是否被缓存
 * @param url 图片URL
 */
export async function isImageCached(url: string): Promise<boolean> {
  try {
    if ('caches' in window) {
      const cacheNames = await caches.keys()

      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        const response = await cache.match(url)
        if (response) {
          console.log(`图片已缓存: ${cacheName} - ${url}`)
          return true
        }
      }
    }

    return false
  } catch (error) {
    console.warn('检测缓存失败:', error)
    return false
  }
}

/**
 * 获取缓存统计信息
 */
export async function getCacheStats(): Promise<{
  totalCaches: number
  totalImages: number
  cacheDetails: Array<{ name: string; imageCount: number }>
}> {
  const stats = {
    totalCaches: 0,
    totalImages: 0,
    cacheDetails: []
  }

  try {
    if ('caches' in window) {
      const cacheNames = await caches.keys()
      stats.totalCaches = cacheNames.length

      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        const keys = await cache.keys()

        const imageCount = keys.filter((request) =>
          request.url.match(/\.(jpg|jpeg|png|gif|webp|svg)(\?|$)/i)
        ).length

        stats.totalImages += imageCount
        stats.cacheDetails.push({ name: cacheName, imageCount })
      }
    }
  } catch (error) {
    console.error('获取缓存统计失败:', error)
  }

  return stats
}

/**
 * 在控制台中运行缓存管理工具
 */
export function runCacheManagerInConsole(): void {
  console.log('🗂️ 缓存管理工具已加载')
  console.log('使用方法:')
  console.log('1. 强制重新加载图片: forceReloadImage("图片URL")')
  console.log('2. 批量强制重新加载: batchForceReload(["URL1", "URL2"])')
  console.log('3. 清除所有图片缓存: clearAllImageCache()')
  console.log('4. 检测图片缓存: isImageCached("图片URL")')
  console.log('5. 获取缓存统计: getCacheStats()')
  console.log('6. 缓存破坏URL: bustImageCache("图片URL")')

  // 添加到全局对象
  ;(window as any).forceReloadImage = forceReloadImage
  ;(window as any).batchForceReload = batchForceReload
  ;(window as any).clearAllImageCache = clearAllImageCache
  ;(window as any).isImageCached = isImageCached
  ;(window as any).getCacheStats = getCacheStats
  ;(window as any).bustImageCache = bustImageCache
  ;(window as any).clearImageCache = clearImageCache
}

// 自动运行
if (typeof window !== 'undefined') {
  runCacheManagerInConsole()
}
