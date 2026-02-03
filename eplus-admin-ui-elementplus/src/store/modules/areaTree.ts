import { defineStore } from 'pinia'
import { store } from '@/store'

export interface areaTreeState {
  areaTree: string[]
  isSetAreaTree: boolean
}

export const useAreaTreeStore = defineStore('areaTree', {
  state: (): areaTreeState => {
    return {
      areaTree: [],
      isSetAreaTree: false
    }
  },
  actions: {
    setAreaTree(data) {
      this.areaTree = data
      this.isSetAreaTree = true
    }
  }
})

export const useAreaTreeStoreWithOut = () => {
  return useAreaTreeStore(store)
}
