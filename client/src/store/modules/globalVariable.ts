import { defineStore } from 'pinia'
import { store } from '@/store'

export interface globalState {
  glbPageSize: number // 每次调接口默认条数
  glbPageSizes: number[] // 分页中可选择条数
  changeType: string
}

export const globalStore = defineStore('global', {
  state: (): globalState => {
    return {
      glbPageSize: 30,
      glbPageSizes: [30, 50, 100, 200, 500, 1000],
      changeType: ''
    }
  },
  actions: {
    setGlbPageSize(data) {
      this.glbPageSize = data
    },
    setGlbPageSizes(data) {
      this.glbPageSizes = data
    },
    setChangeType(data) {
      this.changeType = data
    }
  }
})
export const useGlobalStoreWithOut = () => {
  return globalStore(store)
}
