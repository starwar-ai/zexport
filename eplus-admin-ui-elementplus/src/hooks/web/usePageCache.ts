/**
 * 页面缓存管理
 * 用于处理页面激活/停用时的状态保存和恢复
 * @author 波波
 * @date 2026-01-11
 */

import { onActivated, onDeactivated, onMounted, ref } from 'vue'

export interface PageCacheOptions {
  // 页面激活时的回调
  onActivatedCallback?: () => void
  // 页面停用时的回调
  onDeactivatedCallback?: () => void
  // 是否自动恢复滚动位置
  autoRestoreScroll?: boolean
  // 是否在激活时自动刷新数据
  autoRefreshOnActivated?: boolean
}

export const usePageCache = (options: PageCacheOptions = {}) => {
  const {
    onActivatedCallback,
    onDeactivatedCallback,
    autoRestoreScroll = true,
    autoRefreshOnActivated = false
  } = options

  // 页面是否已激活
  const isActivated = ref(false)
  // 页面是否首次加载
  const isFirstMount = ref(true)
  // 滚动位置
  const scrollPosition = ref({ x: 0, y: 0 })

  // 保存滚动位置
  const saveScrollPosition = () => {
    if (autoRestoreScroll) {
      scrollPosition.value = {
        x: window.scrollX || window.pageXOffset,
        y: window.scrollY || window.pageYOffset
      }
    }
  }

  // 恢复滚动位置
  const restoreScrollPosition = () => {
    if (autoRestoreScroll && !isFirstMount.value) {
      window.scrollTo(scrollPosition.value.x, scrollPosition.value.y)
    }
  }

  // 页面首次挂载
  onMounted(() => {
    isActivated.value = true
    isFirstMount.value = true
  })

  // 页面激活（从缓存中恢复）
  onActivated(() => {
    isActivated.value = true
    
    // 恢复滚动位置
    restoreScrollPosition()
    
    // 执行自定义激活回调
    onActivatedCallback?.()
    
    // 标记不再是首次加载
    if (isFirstMount.value) {
      isFirstMount.value = false
    }
  })

  // 页面停用（进入缓存）
  onDeactivated(() => {
    isActivated.value = false
    
    // 保存滚动位置
    saveScrollPosition()
    
    // 执行自定义停用回调
    onDeactivatedCallback?.()
  })

  return {
    isActivated,
    isFirstMount,
    scrollPosition,
    saveScrollPosition,
    restoreScrollPosition
  }
}

/**
 * 页面数据刷新管理
 * 用于控制页面数据的刷新时机
 */
export const usePageRefresh = () => {
  const refreshKey = ref(0)
  const isRefreshing = ref(false)

  // 刷新页面数据
  const refresh = async (callback?: () => Promise<void>) => {
    if (isRefreshing.value) return
    
    try {
      isRefreshing.value = true
      refreshKey.value++
      
      if (callback) {
        await callback()
      }
    } finally {
      isRefreshing.value = false
    }
  }

  return {
    refreshKey,
    isRefreshing,
    refresh
  }
}
