import { defineStore } from 'pinia'
import { store } from '@/store'

export interface feeListState {
  feeList: []
  isSetFeeList: boolean
}

export const useFeeStore = defineStore('fee', {
  state: (): feeListState => {
    return {
      feeList: [],
      isSetFeeList: false
    }
  },
  actions: {
    setFeeList(data) {
      this.feeList = data
      this.isSetFeeList = true
    }
  }
})

export const useFeeStoreWithOut = () => {
  return useFeeStore(store)
}
