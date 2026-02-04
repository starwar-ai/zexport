import { defineStore } from 'pinia'
import { store } from '@/store'

export interface settlementListState {
  settlementList: string[]
  isSetSettlementList
}

export const useSettlementStore = defineStore('settlement', {
  state: (): settlementListState => {
    return {
      settlementList: [],
      isSetSettlementList: false
    }
  },
  actions: {
    setsettlementList(data) {
      this.settlementList = data
      this.isSetSettlementList = true
    }
  }
})

export const useSettlementStoreWithOut = () => {
  return useSettlementStore(store)
}
