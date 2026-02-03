import { defineStore } from 'pinia'
import { store } from '@/store'

export interface rateListState {
  rateList: { list }
  isSetRateList
}

export const useRateStore = defineStore('rate', {
  state: (): rateListState => {
    return {
      rateList: { list: [] },
      isSetRateList: false
    }
  },
  actions: {
    setRateList(data) {
      this.rateList = data
      this.isSetRateList = true
    }
  }
})

export const useRateStoreWithOut = () => {
  return useRateStore(store)
}
