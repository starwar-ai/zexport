import router from '@/router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { getRawRoute } from '@/utils/routerHelper'
import { defineStore } from 'pinia'
import { store } from '../index'
import { findIndex } from '@/utils'

export interface TagsViewState {
  visitedViews: RouteLocationNormalizedLoaded[]
  cachedViews: Set<string>
}

export const useTagsViewStore = defineStore('tagsView', {
  state: (): TagsViewState => ({
    visitedViews: [],
    cachedViews: new Set()
  }),
  getters: {
    getVisitedViews(): RouteLocationNormalizedLoaded[] {
      return this.visitedViews
    },
    getCachedViews(): string[] {
      return Array.from(this.cachedViews)
    },
    // 获取选中的tag
    getSelectedTag(): RouteLocationNormalizedLoaded | undefined {
      const currentRoute = router.currentRoute.value
      return this.visitedViews.find(v => v.path === currentRoute.path)
    }
  },
  // 持久化配置，防止页面刷新后数据丢失
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'tagsView',
        storage: sessionStorage,
        paths: ['visitedViews']
      }
    ]
  },
  actions: {
    // 新增缓存和tag
    addView(view: RouteLocationNormalizedLoaded): void {
      this.addVisitedView(view)
      this.addCachedView()
    },
    // 新增tag
    addVisitedView(view: RouteLocationNormalizedLoaded) {
      if (this.visitedViews.some((v) => v.path === view.path)) return
      // const index = this.visitedViews.findIndex((v) => v.path === view.path)
      // if (index >= 0) {
      //   this.visitedViews.splice(index, 1)
      // }
      if (view.meta?.noTagsView) return
      this.visitedViews.push(
        Object.assign({}, view, {
          title: view.meta?.title || 'no-name'
        })
      )
    },
    // 新增缓存
    addCachedView() {
      const cacheMap: Set<string> = new Set()
      for (const v of this.visitedViews) {
        const item = getRawRoute(v)
        const needCache = !item.meta?.noCache
        if (!needCache) {
          continue
        }
        const name = item.name as string
        if (name) {
          cacheMap.add(name)
        }
      }
      if (Array.from(this.cachedViews).sort().toString() === Array.from(cacheMap).sort().toString())
        return
      this.cachedViews = cacheMap
    },
    // 恢复缓存（用于页面刷新后）
    restoreCachedViews() {
      this.addCachedView()
    },
    // 删除某个
    delView(view: RouteLocationNormalizedLoaded) {
      this.delVisitedView(view)
      this.delCachedView(view)
    },
    // 删除tag
    delVisitedView(view: RouteLocationNormalizedLoaded) {
      for (const [i, v] of this.visitedViews.entries()) {
        if (v.path === view.path) {
          this.visitedViews.splice(i, 1)
          break
        }
      }
    },
    // 删除缓存
    delCachedView(view?: RouteLocationNormalizedLoaded) {
      const route = view || router.currentRoute.value
      const viewList = this.visitedViews.filter((v) => v.name === route.name)
      if (viewList.length === 0) {
        const index = findIndex<string>(this.getCachedViews, (v) => v === route.name)
        if (index > -1) {
          this.cachedViews.delete(this.getCachedViews[index])
        }
      }
    },
    // 删除所有缓存和tag
    delAllViews() {
      this.delAllVisitedViews()
      this.delCachedView()
    },
    // 删除所有tag
    delAllVisitedViews() {
      // const affixTags = this.visitedViews.filter((tag) => tag.meta.affix)
      this.visitedViews = []
    },
    // 删除其他
    delOthersViews(view: RouteLocationNormalizedLoaded) {
      this.delOthersVisitedViews(view)
      this.addCachedView()
    },
    // 删除其他tag
    delOthersVisitedViews(view: RouteLocationNormalizedLoaded) {
      this.visitedViews = this.visitedViews.filter((v) => {
        return v?.meta?.affix || v.path === view.path
      })
    },
    // 删除左侧
    delLeftViews(view: RouteLocationNormalizedLoaded) {
      const index = findIndex<RouteLocationNormalizedLoaded>(
        this.visitedViews,
        (v) => v.path === view.path
      )
      if (index > -1) {
        this.visitedViews = this.visitedViews.filter((v, i) => {
          return v?.meta?.affix || v.path === view.path || i > index
        })
        this.addCachedView()
      }
    },
    // 删除右侧
    delRightViews(view: RouteLocationNormalizedLoaded) {
      const index = findIndex<RouteLocationNormalizedLoaded>(
        this.visitedViews,
        (v) => v.path === view.path
      )
      if (index > -1) {
        this.visitedViews = this.visitedViews.filter((v, i) => {
          return v?.meta?.affix || v.path === view.path || i < index
        })
        this.addCachedView()
      }
    },
    updateVisitedView(view: RouteLocationNormalizedLoaded) {
      for (let v of this.visitedViews) {
        if (v.path === view.path) {
          v = Object.assign(v, view)
          break
        }
      }
    },
    setVisitedView(list: RouteLocationNormalizedLoaded[]) {
      this.visitedViews = list
    }
  }
})

export const useTagsViewStoreWithOut = () => {
  return useTagsViewStore(store)
}
